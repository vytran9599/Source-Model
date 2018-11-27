import java.io.FileReader;

public class SourceModel {

    private double[][] probMatrix1;
    private String modelName;
    private String fileName;

    public SourceModel(String amodelName, String afileName) throws Exception {
        modelName = amodelName;
        fileName = afileName;
        double[][] charCountMatrix = new double[26][26];
        double[][] probMatrix = new double[26][26];
        FileReader inputStream = new FileReader(fileName);
        int prevNum = -1;
        int currentNum = 0;
        char prevChar;
        char prevLower;
        int prevAscii;
        char currentChar;
        char cLower;
        int currentAscii;
        double count = 0.0;
        System.out.print("Training " + modelName + " model");
        while ((currentNum = inputStream.read()) != -1) {
            if (Character.isAlphabetic((char) currentNum)) {
                if (prevNum == -1) {
                    prevNum = currentNum;
                    prevChar = (char) prevNum;
                    prevLower = Character.toLowerCase(prevChar);
                    prevAscii = prevLower - 'a';
                } else {
                    prevChar = (char) prevNum;
                    prevLower = Character.toLowerCase(prevChar);
                    prevAscii = prevLower - 'a';
                    currentChar = (char) currentNum;
                    cLower = Character.toLowerCase(currentChar);
                    currentAscii = cLower - 'a';
                    charCountMatrix[prevAscii][currentAscii]++;
                    prevNum = currentNum;
                }
            }
        }
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                count = charCountMatrix[i][j] + count;
            }
            for (int k = 0; k < 26; k++) {
                if (count != 0) {
                    probMatrix[i][k] = charCountMatrix[i][k] / count;
                } else {
                    probMatrix[i][k] = 0.01;
                }
                if (probMatrix[i][k] == 0) {
                    probMatrix[i][k] = 0.01;
                }
            }
            count = 0;
        }
        System.out.print(" ... done.");
        System.out.println("");
        probMatrix1 = probMatrix;
    }

    public String getName() {
        return modelName;
    }

    public String toString() {
        double probNum;
        String mStr = "";
        mStr += "     a    b    c    d    e    f    g    h    i    ";
        mStr += "j    k    l    m    n    o    p    q    r    s    t";
        mStr += "    u    v    w    x    y    z";
        mStr += String.format("%n");
        for (int i = 0; i < 26; i++) {
            int iSum = i + 97;
            mStr += Character.toString((char) iSum);
            mStr += " ";
            for (int j = 0; j < 26; j++) {
                probNum = this.probMatrix1[i][j];
                mStr += String.format("%.2f", probNum);
                mStr += " ";
            }
            mStr += String.format("%n");
        }
        return mStr;
    }

    public double probability(String str) {
        double prob = 1.0;
        char[] charArray = str.toCharArray();
        char arrChar2;
        char lowerArrChar;
        char lowerArrChar2;
        int arrAscii1;
        int arrAscii2;
        int firstIndex = 0;
        boolean found = false;
        char first = charArray[0];
        for (int i = 0; i < charArray.length && !found; i++) {
            first = charArray[i];
            if (Character.isAlphabetic(first)) {
                found = true;
                firstIndex = i;
            }
        }
        for (int i = firstIndex + 1; i < charArray.length; i++) {
            char arrChar = charArray[i];
            first = Character.toLowerCase(first);
            lowerArrChar = Character.toLowerCase(arrChar);
            if (Character.isAlphabetic(arrChar)) {
                arrAscii1 = first - 'a';
                arrAscii2 = lowerArrChar - 'a';
                prob = prob * this.probMatrix1[arrAscii1][arrAscii2];
                first = arrChar;
            }
        }
        return prob;
    }

    public static void main(String[] args) throws Exception {
        SourceModel[] corpArr = new SourceModel[args.length - 1];
        double[] sourceProbs = new double[args.length - 1];
        double[] trueProbs = new double[args.length - 1];
        double sum = 0.0;
        double prev = 0.0;
        double next = 0.0;
        int cntLg = 0;
        for (int i = 0; i < args.length - 1; i++) {
            String[] temp = args[i].split("[.]");
            corpArr[i] = new SourceModel(temp[0], args[i]);
            if (i == args.length - 2) {
                System.out.println("Analyzing: " + args[args.length - 1]);
            }
            sourceProbs[i] = corpArr[i].probability(args[args.length - 1]);
            sum = sum + sourceProbs[i];
        }
        for (int j = 0; j < args.length - 1; j++) {
            trueProbs[j] = sourceProbs[j] / sum;
            System.out.printf("Probability that test string is %8s",
                corpArr[j].getName());
            char colon = ':';
            System.out.printf("%-2c", colon);
            System.out.printf("%.2f %n", trueProbs[j]);
            for (int k = 1; k < args.length - 1; k++) {
                if (j == 0) {
                    prev = trueProbs[j];
                } else if (prev > next) {
                    cntLg += 0;
                } else {
                    cntLg = 0;
                    cntLg = cntLg + k - 1;
                    prev = trueProbs[k - 1];
                }
                next = trueProbs[k];
            }
        }
        System.out.println("Test string is most likely "
            + corpArr[cntLg].getName() + ".");
    }

}