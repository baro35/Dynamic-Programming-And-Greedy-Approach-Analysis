import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;


public class Mehmet_Baran_Selcuk_2019510067 {

	public static int getInputs(String inputName) { // getting inputs, return error if entered value
		Scanner sc = new Scanner(System.in);		// is out of bounds.
		int returnValue = 0;

		while (true) {
			if (inputName.equals("gold")) {
				System.out.print("GOLD AMOUNT: ");
				returnValue = sc.nextInt();
				if (returnValue >= 5 && returnValue <= 1200)
					break;
				else
					System.out.println("The GOLD AMOUNT should be within the range 5 and 1200!\n");
			} else if (inputName.equals("maxLevel")) {
				System.out.print("MAX LEVEL ALLOWED: ");
				returnValue = sc.nextInt();
				if (returnValue >= 1 && returnValue <= 9)
					break;
				else
					System.out.println("The MAX LEVEL ALLOWED should be within the range 1 and 9!\n");
			} else {
				System.out.print("NUMBER OF AVAILABLE PIECES PER LEVEL: ");
				returnValue = sc.nextInt();
				if (returnValue >= 1 && returnValue <= 25)
					break;
				else
					System.out
							.println("The NUMBER OF AVAILABLE PIECES PER LEVEL should be within the range 1 and 25!\n");
			}
		}
		return returnValue;
	}

	public static Hero[][] getFileData(Hero[][] heroes, int availablePiecePerLevel, int maxLevel)
			throws FileNotFoundException { //getting data which include in input file, create Hero in order to data we get, 
		Scanner sc = new Scanner(new File("input_1.csv")); //and put them into matrix
		int i = 0;
		int j = -1;
		int counter = 1;
		String type = "";
		sc.nextLine(); // ignoring first line(title)
		while (sc.hasNext()) {
			String[] split = sc.nextLine().split(",");
			if (type.equalsIgnoreCase(split[1])) {
				counter++;
			}
			if (!type.equals(split[1])) {
				counter = 1;
				j++;
				i = 0;
			}
			if (j == maxLevel)
				break;
			type = split[1];
			if (counter <= availablePiecePerLevel) {
				heroes[j][i] = new Hero(split[0], split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]));
				i++;
			}
		}
		sc.close();
		return heroes;
	}
	//Gets matrix and turned it to arraylist
	public static ArrayList<Hero> getArrayList(Hero[][] heroes) {
		ArrayList<Hero> list = new ArrayList<>();
		for (int i = 0; i < heroes.length; i++) {
			for (int j = 0; j < heroes[i].length; j++) {
				list.add(heroes[i][j]);
			}
		}
		return list;
	}
	/////////////////////////////////////RANDOM APPROACH////////////////////////////////////////////////
	public static void randomApproach(Hero[][] heroes, int availablePiecePerLevel, int maxLevel, int gold) {
		ArrayList<Hero> selectedPieces = new ArrayList<>();
		Random rnd = new Random();
		int failCounter = 0; //controls how many fails allowed
		int randomInt1 = 0;
		int randomInt2 = 0;
		boolean[] levelFlag = new boolean[maxLevel];//controls can not take same level pieces at the same time
		
		long startTime = System.nanoTime();
		while (true) {
			randomInt1 = rnd.nextInt(availablePiecePerLevel);
			randomInt2 = rnd.nextInt(maxLevel);
			while (levelFlag[randomInt2] == true) {
				randomInt2 = rnd.nextInt(maxLevel);
				if (levelFlag[randomInt2] != false)
					failCounter++;
				if (failCounter >= 20)// if failcounter reaches 20, break
					break;
			}

			if (gold < heroes[randomInt2][randomInt1].getGold()) // if the gold is not enough
				failCounter++;
			if (failCounter >= 20)
				break;

			if (gold >= heroes[randomInt2][randomInt1].getGold()) {// if the gold is  enough
				selectedPieces.add(heroes[randomInt2][randomInt1]);
				gold -= heroes[randomInt2][randomInt1].getGold();
				levelFlag[randomInt2] = true;
			}
		}
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
        System.out.println("Random Approach algorithm execution time in nanoseconds: " + timeElapsed);
		printSelectedPieces(selectedPieces, "random"); // print random approach
	}
	///////////////////////////////////////////////GREEDY APPROACH/////////////////////////////////////////////
	public static void greedyApproach(ArrayList<Hero> heroes, int availablePiecePerLevel, int maxLevel, int gold) {
		ArrayList<Hero> selectedPieces = new ArrayList<>();

		long startTime = System.nanoTime();//check the level flag, if there has not piece in that level
		for (int i = 0; i < heroes.size(); i++) {//and the gold is enough, take that piece and set that level taken.
			if (gold >= heroes.get(i).getGold() && !Hero.levelCheck(heroes.get(i).getPieceType())) {
				selectedPieces.add(heroes.get(i));
				gold -= heroes.get(i).getGold();
				Hero.levelDef(heroes.get(i).getPieceType());
			}
		}
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
        System.out.println("Greedy Approach algorithm execution time in nanoseconds: " + timeElapsed + "ns");
		printSelectedPieces(selectedPieces, "greedy");
	}

	public static void printSelectedPieces(ArrayList<Hero> selectedPieces, String choice) {
		int totalAttackPoint = 0;
		int goldSpent = 0;

		if (choice.equalsIgnoreCase("dynamic")) {
			System.out.println("User`s Dynamic Programming Approach result");
		}
		else if (choice.equalsIgnoreCase("random"))
			System.out.println("Computer`s Random Approach result");
		else
			System.out.println("Computer`s Greedy Approach result");

		for (int i = 0; i < selectedPieces.size(); i++) {
			if(selectedPieces.get(i)!= null) {
				totalAttackPoint += selectedPieces.get(i).getAttackPoint();
				goldSpent += selectedPieces.get(i).getGold();
			}
		}
		System.out.println("Total attack point: " + totalAttackPoint);
		System.out.println("Total gold spent: " + goldSpent);

		System.out.println("\nSelected Pieces:");
		for (int i = 0; i < selectedPieces.size(); i++) {
			if(selectedPieces.get(i)!= null) {
				System.out.println(selectedPieces.get(i).getName() + " (" + selectedPieces.get(i).getPieceType() + ", "
					+ selectedPieces.get(i).getGold() + " Gold, " + selectedPieces.get(i).getAttackPoint()
					+ " Attack)");
			}
		}
		System.out.println("********************************");
	}
	////////////////////////////////////////////////DYNAMIC PROGRAMMING APPROACH//////////////////////////////////////////
	private static void dynamicApproach(ArrayList<Hero> heroes, int gold, int allPieces, int[] attackPoint, int[] price) {
		// dp[i] is going to store maximum value
		ArrayList<Hero> selectedPieces = new ArrayList<>();
		Hero[][] arr = new Hero[gold+1][allPieces];//DYNAMIC PARCA TABLOSU
		boolean[][] flag = new boolean[gold+1][allPieces]; //debug icin
		int dp[] = new int[gold + 1];
		
		long startTime = System.nanoTime();
		for (int i = 0; i <= gold; i++) {
			for (int j = 0; j < allPieces; j++) {
				if (price[j] <= i) { //parcanin costu yeterli ise
					if(dp[i] >= dp[i - price[j]] + attackPoint[j]) {//tablodan attack point kontrolu
						dp[i] = dp[i];
					}
					else {//yeni parca gelecek						
						boolean typeCheck = false;
						boolean samePieceCheck = false;
						int checkRecord = 0;
						for (int j2 = 0; j2 < allPieces; j2++) {// gelen parcanin typesi onceden secilmis mi
							if(arr[i-price[j]][j2] != null && heroes.get(j).getPieceType().equals(arr[i-price[j]][j2].getPieceType())) {
								typeCheck = true;
								checkRecord = j2;			//gelen parca onceden secilmis mi
								if(heroes.get(j).getName().equals(arr[i-price[j]][j2].getName())) samePieceCheck = true;
								break;
							}
						}
						if(!typeCheck) {//parca tipi onceden secilmediyse
							for (int j2 = 0; j2 < allPieces; j2++) {
								arr[i][j2] = null;
								flag[i][j2] = false;
							}
							dp[i] = dp[i - price[j]] + attackPoint[j];//tabodaki yeni deger
							
							for (int j2 = 0; j2 < allPieces; j2++) {//eski parcalarin ustune yeni parca geldi
								if(arr[i-price[j]][j2] != null)
									arr[i][j2] = heroes.get(j2);
								flag[i][j2] = flag[i-price[j]][j2];
							}
							arr[i][j] = heroes.get(j);// yeni parca
							flag[i][j] = true;
						}
						else if(samePieceCheck) { 		//ayni tas varsa dp
							if(dp[i] < dp[i - price[j]] || dp[i] < attackPoint[j]) {//yeni gelen tasin attack pointi
								for (int j2 = 0; j2 < allPieces; j2++) {//veya tablodaki bottom deger
									arr[i][j2] = null;					//tablodaki yeni degerden yuksek mi
									flag[i][j2] = false;
								}
								if(dp[i - price[j]] >= attackPoint[j]) {
									dp[i] = dp[i - price[j]];
									for (int j2 = 0; j2 < allPieces; j2++) {
										if(arr[i-price[j]][j2] != null)
											arr[i][j2] = heroes.get(j2);
										flag[i][j2] = flag[i-price[j]][j2];
									}
								}
							}
						}
						else {
							//attackpoint buyukse dp  (ayni tur farkli tas gelince)
							if(attackPoint[j] > dp[i]) {
								for (int j2 = 0; j2 < allPieces; j2++) {
									arr[i][j2] = null;
									flag[i][j2] = false;
								}
								dp[i] = attackPoint[j];
								
								for (int j2 = 0; j2 < allPieces; j2++) {
									if(arr[i-price[j]][j2] != null)
										arr[i][j2] = heroes.get(j2);
									flag[i][j2] = flag[i-price[j]][j2];
								}
								arr[i][j] = heroes.get(j);
								flag[i][j] = true;
							}					// ayni tur farkli tas gelince fakat 
							else if(typeCheck) {//yeni tasin degeri tablodaki degeri guncelleyebiliyor ise
								if(arr[i-price[j]][checkRecord].getAttackPoint() < heroes.get(j).getAttackPoint()) {
									if(heroes.get(j).getAttackPoint() > dp[i]) {
										for (int j2 = 0; j2 < allPieces; j2++) {
											arr[i][j2] = null;
											flag[i][j2] = false;
										}
										dp[i] = heroes.get(j).getAttackPoint();
										arr[i][j] = heroes.get(j);
										flag[i][j] = true;
									}
									else {
										if(dp[i - price[j]] > dp[i]) {
											for (int j2 = 0; j2 < allPieces; j2++) {
												arr[i][j2] = null;
												flag[i][j2] = false;
											}
											dp[i] = dp[i - price[j]];
											for (int j2 = 0; j2 < allPieces; j2++) {
												if(arr[i-price[j]][j2] != null)
													arr[i][j2] = heroes.get(j2);
												flag[i][j2] = flag[i-price[j]][j2];
											}
										}
									}
								}
							}
							if(dp[i-1] > dp[i]) {//yeni tasin degeri tablodaki degeri guncelleyemiyor ise
								for (int j2 = 0; j2 < allPieces; j2++) {//tablodaki onceki deger ve taslar						
									arr[i][j2] = null;					//tekrar yazilir
									flag[i][j2] = false;
								}
								dp[i] = dp[i-1];
								for (int j2 = 0; j2 < allPieces; j2++) {
									if(arr[i-1][j2] != null)
										arr[i][j2] = heroes.get(j2);
									flag[i][j2] = flag[i-1][j2];
								}
							}	
						}
					}
				}
			}
		}
		long endTime = System.nanoTime();
		long timeElapsed = endTime - startTime;
        System.out.println("Dynamic Programming Approach algorithm execution time in nanoseconds: " + timeElapsed + "ns");
		for (int k = 0; k < arr[gold].length; k++) {//tablonun son degerindeki parcalarin eklenmesi
			selectedPieces.add(arr[gold][k]);
		}
		printSelectedPieces(selectedPieces, "dynamic");		
	}

	public static void main(String[] args) {
		int GOLD_AMOUNT;
		int MAX_LEVEL_ALLOWED;
		int NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL;

		try {
			GOLD_AMOUNT = getInputs("gold");
			MAX_LEVEL_ALLOWED = getInputs("maxLevel");
			NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL = getInputs("");

			Hero[][] heroes = new Hero[MAX_LEVEL_ALLOWED][NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL];
			int[] attackPoint = new int[NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL* MAX_LEVEL_ALLOWED];
			int[] price = new int[NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL* MAX_LEVEL_ALLOWED];
			ArrayList<Hero> list = new ArrayList<>();
			
			heroes = getFileData(heroes, NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL, MAX_LEVEL_ALLOWED);
			list = getArrayList(heroes);
			//listenin attack point attributesine gore reverse sort edilmesi(buyukten kucuge)
			Collections.sort(list, Collections.reverseOrder(Comparator.comparing(Hero::getAttackPoint)));
			
			for (int i = 0; i < list.size(); i++) {
				attackPoint[i] = list.get(i).getAttackPoint();
				price[i] = list.get(i).getGold();
			}

			System.out.println("\n---------------------------------------------");
			System.out.println("GOLD AMOUNT: " + GOLD_AMOUNT + "\nMAX LEVEL ALLOWED: " + MAX_LEVEL_ALLOWED + "\n"
					+ "NUMBER OF AVAILABLE PIECES PER LEVEL: " + NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL + "\n"
					+ "---------------------------------------------");

			System.out.println("======================TRIAL #1======================");
			dynamicApproach(list, GOLD_AMOUNT, NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL* MAX_LEVEL_ALLOWED, attackPoint, price);
			greedyApproach(list, NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL, MAX_LEVEL_ALLOWED, GOLD_AMOUNT);
			 
			System.out.println("======================TRIAL #2======================");
			dynamicApproach(list, GOLD_AMOUNT, NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL* MAX_LEVEL_ALLOWED, attackPoint, price);       
			randomApproach(heroes, NUMBER_OF_AVAILABLE_PIECES_PER_LEVEL, MAX_LEVEL_ALLOWED, GOLD_AMOUNT);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
	public static class Hero {
		private String name;
		private String pieceType;
		private int gold;
		private int attackPoint;
		public static boolean[] levelFlag = new boolean[9]; //flag for greedy
		
		public Hero(String name, String pieceType, int gold, int attackPoint) {
			this.name = name;
			this.pieceType = pieceType;
			this.gold = gold;
			this.attackPoint = attackPoint;
		}

		public String getName() {
			return name;
		}

		public String getPieceType() {
			return pieceType;
		}

		public int getGold() {
			return gold;
		}

		public int getAttackPoint() {
			return attackPoint;
		}
		
		public static boolean levelCheck(String pieceType) {//check and return that level flag
			if(pieceType.equalsIgnoreCase("Pawn")) return levelFlag[0];
			else if(pieceType.equalsIgnoreCase("Rook")) return levelFlag[1];
			else if(pieceType.equalsIgnoreCase("Archer")) return levelFlag[2];
			else if(pieceType.equalsIgnoreCase("Knight")) return levelFlag[3];
			else if(pieceType.equalsIgnoreCase("Bishop")) return levelFlag[4];
			else if(pieceType.equalsIgnoreCase("War_ship")) return levelFlag[5];
			else if(pieceType.equalsIgnoreCase("Siege")) return levelFlag[6];
			else if(pieceType.equalsIgnoreCase("Queen")) return levelFlag[7];
			else if(pieceType.equalsIgnoreCase("King")) return levelFlag[8];
			return false;
		}
		
		public static void levelDef(String pieceType) {//define level flag as true
			if(pieceType.equalsIgnoreCase("Pawn")) levelFlag[0] = true;
			else if(pieceType.equalsIgnoreCase("Rook")) levelFlag[1] = true;
			else if(pieceType.equalsIgnoreCase("Archer")) levelFlag[2] = true;
			else if(pieceType.equalsIgnoreCase("Knight")) levelFlag[3] = true;
			else if(pieceType.equalsIgnoreCase("Bishop")) levelFlag[4] = true;
			else if(pieceType.equalsIgnoreCase("War_ship")) levelFlag[5] = true;
			else if(pieceType.equalsIgnoreCase("Siege")) levelFlag[6] = true;
			else if(pieceType.equalsIgnoreCase("Queen")) levelFlag[7] = true;
			else if(pieceType.equalsIgnoreCase("King")) levelFlag[8] = true;
		}
		
		public static void refreshLevelFlag() {//clear all levels
			levelFlag = new boolean[9];
		}

	}


}

