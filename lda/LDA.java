import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Random;
import org.apache.commons.math.special.Gamma;

public class LDA extends TopicModel {

	public HashMap<String,Integer> wordMap;
	public HashMap<Integer,String> wordMapInv;

	public String[] docsC1;
	public String[] docsC2;
	public int[][] docs;
	public int[][] docsZ;
	public int[][] docsX;

	public int[][] nDZ;
	public int[] nD;
	public int[][] nZW;
	public int[] nZ;
	public int[] nBW;
	public int nB;
	public int[] nX;

	public int D; //number of docs
	public int W; //number of words
	public int Z; //number of topics
	
	public double beta;
	public double[] alpha;
	public double alphaSum;
	public double gamma0;
	public double gamma1;
	
	public LDA(int z, double a, double b, double g0, double g1) {
		beta = b;
		gamma0 = g0;
		gamma1 = g1;
		Z = z;
		alpha = new double[Z];
		for (int i = 0; i < Z; i++) alpha[i] = a;
		alphaSum = a*Z;
	}
	
	public void initialize() {
		System.out.println("Initializing...");
		Random r = new Random();


		docsZ = new int[D][];
		docsX = new int[D][];

		nDZ = new int[D][Z];
		nD = new int[D];
		nZW = new int[Z][W];
		nZ = new int[Z];
		nBW = new int[W];
		nB = 0;
		nX = new int[2];
		
		for (int d = 0; d < D; d++) {
			docsZ[d] = new int[docs[d].length];
			docsX[d] = new int[docs[d].length];
			
			for (int n = 0; n < docs[d].length; n++) {
				int w = docs[d][n];

				int z = r.nextInt(Z);		// select random z value in {0...Z-1}
				docsZ[d][n] = z;

				//int x = r.nextInt(2);		// select x uniformly
				int x = 0;
				double u = r.nextDouble();		// select random x value in {0,1}
				u *= (double)(gamma0+gamma1);		// from distribution given by prior
				if (u > gamma0) x = 1;
				x = 1;
				docsX[d][n] = x;
				
				// update counts
				
				nX[x] += 1;
				
				if (x == 0) {
					nBW[w] += 1;
					nB += 1;
				}
				else {
					nDZ[d][z] += 1;
					nD[d] += 1;
					nZW[z][w] += 1;	
					nZ[z] += 1;				
				}
			}
		}
	}

	public void updateAlpha() {
		for (int i = 0; i < 1; i++) {
			double[] alpha0 = new double[Z];
			double alpha0D = 0;

			for (int d = 0; d < D; d++) {
				if (nD[d] == 0) continue;
				for (int z = 0; z < Z; z++) {
					alpha0[z] += Gamma.digamma(nDZ[d][z]+0.0 + alpha[z]) - Gamma.digamma(alpha[z]);
				}
				alpha0D += Gamma.digamma(nD[d]+0.0*Z + alphaSum) - Gamma.digamma(alphaSum);
			}

			alphaSum = 0;

			for (int z = 0; z < Z; z++) {
				if (alpha0D > 0) alpha[z] = alpha[z] * (alpha0[z] / alpha0D);
				else alpha[z] = 0.000001;
				alpha[z] += 0.000001;
				System.out.println("alpha_"+z+" = "+alpha[z]);

				alphaSum += alpha[z];
			}
		}

	}

	public void updateBeta()
	{
		double LLold = 0;
		double LLnew = 0;
		double betaSum = beta*(double)W;

		Random r = new Random();
                double betaNew = Math.exp(Math.log(beta) + r.nextGaussian());
                double betaSumNew = betaNew * (double)W;

		for (int z = 0; z < Z; z++) {
			LLold += Gamma.logGamma(betaSum) - Gamma.logGamma(nZ[z] + betaSum);
			LLnew += Gamma.logGamma(betaSumNew) - Gamma.logGamma(nZ[z] + betaSumNew);

			for (int w = 0; w < W; w++) {
				LLold += Gamma.logGamma(nZW[z][w] + beta) - Gamma.logGamma(beta);
				LLnew += Gamma.logGamma(nZW[z][w] + betaNew) - Gamma.logGamma(betaNew);
			}
		}

		double ratio = Math.exp(LLnew - LLold);

		boolean accept = false;
		if (r.nextDouble() < ratio) accept = true;
		if (betaNew > 0.5) accept = false; // hack

		System.out.println("beta: proposed "+betaNew);
		System.out.println(" (ratio = "+ratio);
		if (accept) {
			beta = betaNew;
			System.out.println("Accepted");
		} else {
			System.out.println("Rejected");
		}
		System.out.println("beta: "+beta);
	}
	
	
	public void doSampling(int iter) {
		long startTimeS = System.currentTimeMillis();
		for (int d = 0; d < D; d++) {
			for (int n = 0; n < docs[d].length; n++) {
				sample(d, n);
			}
		}

		if (iter > 20 && iter % 10 == 0) updateAlpha();
		if (iter >= 100 && iter % 10 == 0) updateBeta();

		long endTimeS = System.currentTimeMillis();
		double sec = (double)(endTimeS-startTimeS)/1000.0;
		System.out.println("time per iter: "+sec);
	}
	
	public void sample(int d, int n) {
		int w = docs[d][n];
		int topic = docsZ[d][n];
		int level = docsX[d][n];
		
		// decrement counts

		nX[level] -= 1;

		if (level == 0) {
			nBW[w] -= 1;
			nB -= 1;
		} else {
			nDZ[d][topic] -= 1;
			nD[d] -= 1;
			nZW[topic][w] -= 1;
			nZ[topic] -= 1;
		}

		double betaNorm = W * beta;

		// sample new value for level
		
		double pTotal = 0.0;
		double[] p = new double[Z+1];
	
		// this will be p(x=0)	
		p[Z] = (nX[0] + gamma0) *
			(nBW[w] + beta) / (nB + betaNorm);
		p[Z] = 0; //disable
		pTotal += p[Z];
		
		// sample new value for topic and level
	
		for (int z = 0; z < Z; z++) {
			p[z] = (nX[1] + gamma1) * 
				(nDZ[d][z] + alpha[z]) / (nD[d] + alphaSum) *
				(nZW[z][w] + beta) / (nZ[z] + betaNorm);
			pTotal += p[z];
		}

		Random r = new Random();
		double u = r.nextDouble() * pTotal;
		
		double v = 0.0;
		for (int z = 0; z < Z+1; z++) {
			v += p[z];
			
			if (v > u) {
				topic = z;
				break;
			}
		}

		if (topic == Z) level = 0;
		else level = 1;
		
		// increment counts

		nX[level] += 1;

		if (level == 0) {
			nBW[w] += 1;
			nB += 1;
		} else {
			nDZ[d][topic] += 1;
			nD[d] += 1;
			nZW[topic][w] += 1;
			nZ[topic] += 1;
		}
		
		// set new assignments

		docsZ[d][n] = topic;
		docsX[d][n] = level;
	}

	public void readDocs(String filename) throws Exception {
		System.out.println("Reading input...");
		
		wordMap = new HashMap<String,Integer>();
		wordMapInv = new HashMap<Integer,String>();
		
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr); 

		String s;
	
		D = 0;
		int dj = 0;
		while((s = br.readLine()) != null) {
			//if (dj++ % 20 != 0) continue;
			D++;
		}

		docsC1 = new String[D];
		docsC2 = new String[D];
		docs = new int[D][];

		fr = new FileReader(filename);
		br = new BufferedReader(fr); 

		int d = 0;
		int di = 0;
		while ((s = br.readLine()) != null) {
			//if (di++ % 20 != 0) continue;
			String[] tokens0 = s.split("\\s+");
			String [] tokens = new String[tokens0.length-2];
			for (int n = 2; n < tokens0.length; n++) tokens[n-2] = tokens0[n];			
			
			int N = tokens.length;

			docsC1[d] = tokens0[0];			
			docsC2[d] = tokens0[1];			
			docs[d] = new int[N];
			
			for (int n = 0; n < N; n++) {
				String word = tokens[n];
				String[] parts = word.split(":");
				word = parts[0];

				int key = wordMap.size();
				if (!wordMap.containsKey(word)) {
					wordMap.put(word, new Integer(key));
					wordMapInv.put(new Integer(key), word);
				}
				else {
					key = ((Integer) wordMap.get(word)).intValue();
				}
				
				docs[d][n] = key;
			}
			
			d++;
		}
		
		br.close();
		fr.close();
		
		W = wordMap.size();

		System.out.println(D+" documents");
		System.out.println(W+" word types");
	}

	public void writeOutput(String filename) throws Exception {
		System.out.println("Writing output...");

		FileWriter fw = new FileWriter(filename+".assign");
		BufferedWriter bw = new BufferedWriter(fw); 		

		for (int d = 0; d < D; d++) {
			bw.write(docsC1[d]+" ");
			bw.write(docsC2[d]+" ");

			for (int n = 0; n < docs[d].length; n++) {
				String word = wordMapInv.get(docs[d][n]);
				bw.write(word+":"+docsZ[d][n]+":"+docsX[d][n]+" ");
			}
			bw.newLine();
		}
		
		bw.close();
		fw.close();

		fw = new FileWriter(filename+".alpha");
		bw = new BufferedWriter(fw); 		

		for (int z = 0; z < Z; z++) {
			bw.write(""+alpha[z]);
			bw.newLine();
		}
		
		bw.close();
		fw.close();

		fw = new FileWriter(filename+".beta");
		bw = new BufferedWriter(fw); 		

		bw.write(""+beta);
		
		bw.close();
		fw.close();
	}
}
