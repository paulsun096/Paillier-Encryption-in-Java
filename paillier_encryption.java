import java.math.BigInteger;
import java.util.Random;

//PERFORM SECURE MULTIPLICATION

public class PE2 extends PlainTextArray {
	//case 0:
	//a = r.pow(N);
	//a.mod(n2);

	//case 1:
	//b = g.multiply(r.pow(N));
	//b.mod(n2);

	public static Integer n = 1000; //m
	//array of documents

	public static BigInteger[] a = PlainTextArray.list(n);
	public static BigInteger[] b = PlainTextArray.list(n);

	public static void main(String[] args) {
		BigInteger  ea[], eb[], x;
		int i=0;
		//ar = PlainTextArray.list(N);

		ea = new BigInteger[n];
		eb = new BigInteger[n];

		//System.out.println("----------------");
		//System.out.println("E(a)");
		for(i = 0; i < n; i++){
			ea[i] = encrypt(a[i]);
		}
		//System.out.println("----------------");
		//System.out.println("E(b)");
		for(i = 0; i < n; i++){
			eb[i] = encrypt(b[i]);
		}

		//System.out.println("Union of sets A and B: ");
		for(i=0; i<n; i++) {
			//send Enc(a), Enc(b) to
			//System.out.print("union of: " + enc_arr1[i] + " * " + enc_arr2[i]);
			union(ea[i], eb[i]);
		}

		//run encrypt
		//E(a);

	}

	public static void E(BigInteger c[]) {
		BigInteger ec[];
		ec = new BigInteger[n];

		for(int i=0; i < n; i++){
			c[i] = encrypt(c[i]);
		}
		/*
		 for(int i=0; i < numEnc; i++){
			c[i] = encrypt(c[i]);
		} */
	}

	//ENCRYPTION
	public static BigInteger encrypt(BigInteger input) {
		Integer key_length = 1024;

		BigInteger g, r, N, n_pq, n2, p, q;

		p = new BigInteger(key_length/2, new Random());
		q = new BigInteger(key_length/2, new Random());

		//nfrom key size 512
		n_pq = p.multiply(q);

		BigInteger zero = new BigInteger("0");
		Random rand = new Random();
		Integer ran = rand.nextInt(n-1)+1;

		//n = size of array
		g = BigInteger.valueOf(n+1);
		r = BigInteger.valueOf(ran.intValue());

		Integer exp = 2;
		N = new BigInteger(""+n+"");
		n2 = n_pq.multiply(n_pq);
		//System.out.print("E(" + input+"): " );
		//ENCRYPTION
		for(int i=0; i < n; i++){
			if(input.compareTo(zero) == 0) {
			    input = r.modPow(n_pq, n2);
			} else {
				//input = g.multiply(r.pow(n)).mod(n2);

				input = g.multiply(r.modPow(n_pq, n2));
			}
		}
		//System.out.println(input);
		return input;
	}


	//better way to inherit 'N'
	public static void union(BigInteger enc_a, BigInteger enc_b) {
		Random rand = new Random();

		Integer inta, intb, ra2, rb2; // neg_ra, neg_rb,
		inta = rand.nextInt(n-1)+1;
		intb = rand.nextInt(n-1)+1;

		//convert 'ra' big integer, so can be passed to encrypt method
		BigInteger enc_ra, enc_rb, ra, rb;
		ra = new BigInteger(""+inta+"");
		rb = new BigInteger(""+intb+"");

		//System.out.println("ra: "+ ra + ", rb: "+ rb);

		enc_ra = encrypt(ra);
		enc_rb = encrypt(rb);

		//System.out.println("\n"+"E(ra): "+ enc_ra + ", E(rb): "+ enc_rb);

		//first, enc_a * enc_ra (a+ra)
		BigInteger enc_sum_a_ra, enc_sum_b_rb, enc_sum, enc_neg_a_rb, enc_neg_b_ra, enc_neg_ra_rb;

		//E(a+ra)
		enc_sum_a_ra = enc_ra.multiply(enc_a);
		enc_sum_b_rb = enc_rb.multiply(enc_b);

		//E(a+ra)*E(b+rb)
		enc_sum = enc_sum_a_ra.multiply(enc_sum_b_rb);

		/*x = (a+ra)*(b+rb)
		Cloud 2 Decrypts E(a+ra) & E(b+rb)
		*/

		//E(x) = E((a+ra)*(b+rb))

		//-ra, -rb
		//neg_ra = inta * -1;
		//neg_rb = intb * -1;

		//System.out.println("-ra: " + neg_ra + ", -rb: " + neg_rb);
		//***********************
		//|-ra| |-rb|
		ra2 = n - ra.intValue();
		rb2 = n - rb.intValue();
		//**********************

		//System.out.println("\n"+"|ra|: "+ra2 + ", |ra|: " + (rb2));

		//E(-arb)
		//n_ra = new BigInteger(""+ra2+"");
		//n_rb = new BigInteger(""+rb2+"");

		//System.out.println("\n"+"E(a+ra) * E(b+rb) = E(ab+arb+bra+rarb): " + enc_sum);

		//Using the positive equivalent in place of negative exponenet
		//E(a)^-rb 	-->  E(-arb)
		enc_neg_a_rb = enc_a.pow(rb2);
		//System.out.println("\n"+"E(-arb): " + enc_neg_a_rb);

		//E(-bra)
		enc_neg_b_ra = enc_b.pow(ra2);
		//System.out.println("E(-bra): " + enc_neg_b_ra );

		//E(-rarb)
		enc_neg_ra_rb = enc_ra.pow(rb2);
		//System.out.println("\n"+"E(-rarb): "+enc_neg_ra_rb);

		//enc_sum: E(ab+arb+bra+rarb) * E(-arb)
		enc_sum = enc_sum.multiply(enc_neg_a_rb);
		//System.out.println("\n"+"E(ab+arb+bra+rarb) * E(-arb): "+enc_sum);

		//enc_sum: E(ab+arb+bra+rarb) * E(-arb) * E(-bra)
		enc_sum = enc_sum.multiply(enc_neg_b_ra);
		//System.out.println("E(ab+arb+bra+rarb) * E(-arb) * E(-bra) = " +enc_neg_b_ra);

		//enc_sum: E(ab+arb+bra+rarb) * E(-arb) * E(-bra) * E(-rarb)
		enc_sum = enc_sum.multiply(enc_neg_ra_rb);
		//System.out.println("E(ab+arb+bra+rarb) * E(-arb) * E(-bra) * E(-rarb) = " + enc_neg_ra_rb + "\n");

		//Result differ form validation
		//calling new instance of encrypt uses different value for r than
	}
}
