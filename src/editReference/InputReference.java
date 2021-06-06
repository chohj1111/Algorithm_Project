package editReference;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import static main.ReferenceDNA.file_size;

public class InputReference {
    /*
    void형 createRefDNA 함수
    rand함수를 통해 길이가 1000000인 ReferenceDNA를 담은 "ReferenceDNA.txt" 생성
    입력 :
    	없음
    출력 :
    	생성한 reference DNA로 "ReferenceDNA.txt"로 출력
    */
    public static void createRefDNA() {
        char[] referenceDNA = new char[file_size];  // reference DNA 저장할 변수
        int tmp, i, j;
        char tmpChar = '0';
        Random r = new Random();
        // 기존에 건너뜀 없이 일렬로 저장되던 데이터를 4칸씩 건너뛰면서 저장하게 변경
        for (i = 0; i < 4; i++) {
            j = i;
            while (j < file_size) {
                tmp = r.nextInt(4);
                switch (tmp) {
                    case (0):
                        tmpChar = 'A';
                        break;
                    case (1):
                        tmpChar = 'C';
                        break;
                    case (2):
                        tmpChar = 'G';
                        break;
                    case (3):
                        tmpChar = 'T';
                        break;
                }
                referenceDNA[j] = tmpChar;
                j += 4;
            }
        }

        // ReferenceDNA 출력
        File file = new File("input/ReferenceDNA.txt");
        String strForFile = new String(referenceDNA);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(strForFile);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
 void형 getMyRandomReference
 FILEPATH의 문자열을 입력받아 임의로 생성된 SNP으로
 My DNA Reference를 만드는 함수
 입력 :
     원본 data의 경로 = FILEPATH
 출력 :
     MyDNA.txt 파일 생성
 */
    public static void getMyReference(String FILEPATH) {
        char myDNA[] = new char[file_size];   // My DNA Reference 저장
        int myDNA_len;                       // My DNA Reference 길이
        char charset[] = new String("ATGC").toCharArray(); // string에 들어갈 charset
        int SNP_num = file_size/100*3;      // My DNA Sequence 생성할 때 원본 sequence와 다른 문자 개수

        // FILEPATH로부터 Reference DNA 입력
        StringBuilder stringBuilder = new StringBuilder();
        String temp_str = "";
        try {
            FileReader fr = new FileReader(FILEPATH);
            BufferedReader br = new BufferedReader(fr);

            while ((temp_str = br.readLine()) != null) { // read line from file
                stringBuilder.append(temp_str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        myDNA = stringBuilder.toString().toCharArray(); //  convert StringBuilder to char array
        myDNA_len = myDNA.length;   // myDNA의 길이

        // random seed
        Random r = new Random();

        // 임의의 SNP 생성 후 변경하여 My DNA Reference 생성
        for (int i = 0; i < SNP_num; i++) {
            // randomIndex의 원소를 randomChar로 변경
            int randomIndex = r.nextInt(myDNA_len);
            char randomChar = charset[r.nextInt(charset.length)];
            myDNA[randomIndex] = randomChar;
        }

        // 위에서 일부분을 변경한 My DNA Reference로 "MyDNA.txt" 생성
        String myDNA_str = new String(myDNA);
        File file = new File("refer/MyDNA.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(myDNA_str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     void형 makeSpectrum
     My DNA Reference를 입력받아 k 길이, n개의 shortread를 생성하는 함수
     입력 :
        sequence의 길이 int형 k
        sequence의 개수 int형 n
     출력 :
        myDNASequence.txt 파일 출력
    */
    public static void makeSpectrum(int k, int n) {
        char[] myDNA = new char[file_size];  // my DNA reference 저장
        int myDNA_len;                      // My DNA Reference 길이
        ArrayList<String> myDNA_sequence = new ArrayList<String>();  // my DNA reference를 통해
        // 생성한 shortRead를 Arraylist로 저장

        // My DNA Reference 파일로 입력 받기
        StringBuilder stringBuilder = new StringBuilder();
        String temp_str = "";
        try {
            FileReader fr = new FileReader("refer/MyDNA.txt");
            BufferedReader br = new BufferedReader(fr);

            while ((temp_str = br.readLine()) != null) { // read line from file
                stringBuilder.append(temp_str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        myDNA = stringBuilder.toString().toCharArray(); //  convert StringBuilder to char array
        myDNA_len = myDNA.length;   // myDNA의 길이


        // Random Object
        Random r = new Random();


        // MY DNA Reference의 ShortRead 생성
        for (int i = 0; i < n; i++) {
            // randomIndex부터 k 길이만큼의 부분 문자열 생성
            int randomIndex = r.nextInt(myDNA_len - k);
            String temp = new String(myDNA, randomIndex, k);

            if(!temp.matches("[ATGC]+")){ // 만약 sequencing 결과에 ATGC 제외한
                // 다른 문자 포함 시 다시 sequencing 실시
                i--;
            }else{                              // ATGC만 존재하면 Arraylist에 추가
                myDNA_sequence.add(temp);
            }
        }

        // 위에서 생성한 shortRead로 "MyDNAsequence.txt" 생성
        try {
            FileWriter writer = new FileWriter("refer/myDNASequence.txt");
            for (String str : myDNA_sequence) {
                writer.write(str + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void mutationDNA(String FILEPATH) {
        char referenceDNA[] = new char[file_size];   // My DNA Reference 저장
        int referenceDNA_len;                       // My DNA Reference 길이
        char charset[] = new String("ATGC").toCharArray(); // string에 들어갈 charset
        int mutation_num = file_size/10000*6;      // My DNA Sequence 생성할 때 원본 sequence와 다른 문자 개수

        // FILEPATH로부터 Reference DNA 입력
        StringBuilder stringBuilder = new StringBuilder();
        String temp_str = "";
        try {
            FileReader fr = new FileReader(FILEPATH);
            BufferedReader br = new BufferedReader(fr);

            while ((temp_str = br.readLine()) != null) { // read line from file
                stringBuilder.append(temp_str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        referenceDNA = stringBuilder.toString().toCharArray(); //  convert StringBuilder to char array

        referenceDNA_len = referenceDNA.length;   // myDNA의 길이

        // random seed
        Random r = new Random();

        // 임의의 SNP 생성 후 변경하여 My DNA Reference 생성
        for (int i = 0; i < mutation_num; i++) {
            // randomIndex의 원소를 randomChar로 변경
            int randomIndex = r.nextInt(referenceDNA_len-30);
            for(int j=0;j<30;j++) {
                char randomChar = charset[r.nextInt(charset.length)];
                referenceDNA[randomIndex + j] = randomChar;
            }
        }

        // 위에서 일부분을 변경한 My DNA Reference로 "MutationReferenceDNA.txt" 생성
        String referenceDNA_str = new String(referenceDNA);
        File file = new File("refer/MutationReferenceDNA.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(referenceDNA_str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
