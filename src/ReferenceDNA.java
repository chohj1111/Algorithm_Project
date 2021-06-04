import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;


public class ReferenceDNA {

    static int str_Size = 10000000;
	
	/*
    void형 createRefDNA 함수
    rand함수를 통해 길이가 1000000인 ReferenceDNA를 담은 "ReferenceDNA.txt" 생성
    입력 :
    	없음
    출력 :
    	생성한 reference DNA로 "ReferenceDNA.txt"로 출력
    */
    static void createRefDNA() {
        char[] referenceDNA = new char[str_Size];  // reference DNA 저장할 변수
        int tmp, i, j;
        char tmpChar = '0';
        Random r = new Random();
        // 기존에 건너뜀 없이 일렬로 저장되던 데이터를 4칸씩 건너뛰면서 저장하게 변경
        for (i = 0; i < 4; i++) {
            j = i;
            while (j < str_Size) {
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
        File file = new File("ReferenceDNA.txt");
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
    static void getMyReference(String FILEPATH) {
        char[] myDNA = new char[str_Size];   // My DNA Reference 저장
        int myDNA_len;                       // My DNA Reference 길이
        char charset[] = new String("ATGC").toCharArray(); // string에 들어갈 charset
        int SNP_num = str_Size/100*3;      // My DNA Sequence 생성할 때 원본 sequence와 다른 문자 개수

        // FILEPATH로부터 Reference DNA 입력
        String temp_str = new String("");
        try {
            FileReader fr = new FileReader(FILEPATH);
            BufferedReader br = new BufferedReader(fr);

            while ((temp_str = br.readLine()) != null) {
                myDNA = temp_str.toCharArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        File file = new File("MyDNA.txt");
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
        원본 data의 경로 = FILEPATH
     출력 :
        myDNASequence.txt 파일 출력
    */
    static void makeSpectrum(int k, int n) {
        char[] myDNA = new char[str_Size];  // my DNA reference 저장
        int myDNA_len;                      // My DNA Reference 길이
        ArrayList<String> myDNA_sequence = new ArrayList<String>();  // my DNA reference를 통해
                                                                     // 생성한 shortRead를 Arraylist로 저장

        // My DNA Reference 파일로 입력 받기
        String temp_str = new String("");
        try {
            FileReader fr = new FileReader("MyDNA.txt");
            BufferedReader br = new BufferedReader(fr);

            while ((temp_str = br.readLine()) != null) {
                myDNA = temp_str.toCharArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        myDNA_len = myDNA.length;  // myDNA 길이

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
            FileWriter writer = new FileWriter("myDNASequence.txt");
            for (String str : myDNA_sequence) {
                writer.write(str + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    void형 Trivial 함수
    Brute-Force로 구현
    알고리즘 대조를 위한 기준이 되는 함수
    입력 :
        원본 data의 경로 = FILEPATH
        sequence의 길이 int형 k
        sequence의 개수 int형 n
    출력 :
        없음
    */
    static void Trivial(String FILEPATH, int k, int n) {
        int i, j, m, SNP; // i : for문,
                             // j : reference DNA index
                             // m : sequence index
                             // SNP : reference DNA와 sequence 사이 miss match count
        char[] referenceDNA = new char[str_Size]; // Reference DNA
        int reference_len;                        // referenceDNA length
        char[] restoreDNA = new char[str_Size];   // 복원 후 문자열
        
        // 복원될 문자열의 빈부분을 '?'로 채우기
        for (i = 0; i < str_Size; i++) {
            restoreDNA[i] = '?';
        }

        // FILEPATH로부터 Reference DNA 입력
        String temp_str = new String("");
        try {
            FileReader fr = new FileReader(FILEPATH);
            BufferedReader br = new BufferedReader(fr);

            while ((temp_str = br.readLine()) != null) {
                referenceDNA = temp_str.toCharArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        reference_len = referenceDNA.length;   // myDNA의 길이


        // "MyDNAsequence.txt" 입력받기
        String sequence = "";
        try {
            File file = new File("MyDNAsequence.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((sequence = bufferedReader.readLine()) != null) {    // 한 줄씩 입력받음
            	for(j = 0; j < str_Size - k + 1; j++) {  // reference DNA 순회
            		SNP = 0; // miss match 0으로 set
            		// 문자열 비교
            		for(m = 0; m < k; m++) {
            			if(referenceDNA[j + m]!=sequence.charAt(m)) {  // 다른 문자 check
            				SNP++;
            			}
            			// 스닙이 4개 보다 더 나오면 불일치이므로 바로 break
            			if(SNP > 4) {
            				break;
            			}
            		}
            		// reference와 shortRead 비교 이후에도 스닙이 4개 이하일때 restoreDNA에 값 저장
            		if (SNP <= 4) {
            			for (m = 0; m < k; m++) {
            				restoreDNA[j + m] = sequence.charAt(m);
            			}
            		}
            	}
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // sequence의 내용을 위치에 맞게 기록한 restoreDNA를 "RestoreDNA.txt"로 출력
        File file = new File("RestoreDNA.txt");
        String strForFile = new String(restoreDNA);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(strForFile);
            writer.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    /*
  void형 createIndex 함수
  "ReferenceDNA.txt"를 한칸씩 넘어가며 10개의 문자를 읽어서 해당 문자열의 위치를 기록하는 인덱스 생성
  입력 :
      인덱스를 저장할 indexNode**형 index
      원본 data의 경로 = FILEPATH

  출력 :
      없음
  */
    static LinkedList<Integer>[] createIndex(String FILEPATH) {
        int i, indNum, D;
        int tmp = 0;
        int size = 1;
        char[] referenceDNA = new char[10000000];
        int[] forIndex = new int[10000000];

        // 인덱스의 크기인 size = 4^10
        for (i = 0; i < 10; i++) {
            size *= 4;
        }

        //인덱스는 인접리스트 방식으로 구현
        LinkedList<Integer> indexNode[] = new LinkedList[size];

        // "ReferenceDNA.txt" 입력받기
        String temp_str = new String("");
        try {
            FileReader fr = new FileReader(FILEPATH);
            BufferedReader br = new BufferedReader(fr);

            while ((temp_str = br.readLine()) != null) {
                referenceDNA = temp_str.toCharArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 문자열을 계산이 쉽도록 int형 배열로 변환
        for (i = 0; i < 10000000; i++) {
            switch (referenceDNA[i]) {
                case ('A'):
                    tmp = 0;
                    break;
                case ('C'):
                    tmp = 1;
                    break;
                case ('G'):
                    tmp = 2;
                    break;
                case ('T'):
                    tmp = 3;
                    break;
            }
            forIndex[i] = tmp;
        }

        // 위에서 계산한 int형 배열을 바탕으로 처음의 10자리 수를 10진법으로 계산 (계산전 4진법)
        // 문자열을 비교하는것이아닌 라빈카프알고리즘처럼 수를 계산해서 하면 빠를 것 으로 예상
        indNum = 0;
        for (i = 0; i < 10; i++) {
            indNum *= 4;
            indNum += forIndex[i];
        }

        //처음의 10자리의 위치 indexNode에 넣기
        indexNode[indNum] = new LinkedList<Integer>();
        indexNode[indNum].add(0);


        D = size / 4;
        for (i = 1; i < 10000000 - 10 + 1; i++) {
            // 라빈카프 알고리즘의 방식처럼 10자리의 위치를 모두 구하는것이 아닌
            // 연산을 통해 밀려난것을 빼주고, 자릿수를 고쳐주고, 새롭게 들어오는 부분을 더해주는 방식으로 indNum을 계산
            indNum = (indNum - forIndex[i - 1] * D) * 4 + forIndex[i + 9];

            // 위에서 구한 indNum을 통해 index에 넣기
            if (indexNode[indNum] == null)
                indexNode[indNum] = new LinkedList<Integer>();
            indexNode[indNum].add(i);
        }
        return indexNode;
    }

    /*
       void형 NewAlgorithm 함수
       createIndex에서 생성된 index를 통해 sequence를 비교하여 문자열 복원
       입력 :
           sequence의 길이 int형 k
           sequence의 개수 int형 n
           원본 data의 경로 = FILEPATH

       출력 :
           없음
       */
    static void NewAlgorithm(String FILEPATH, int k, int n) {
        int i, j, m, h, indNum, SNP, count, start, end;
        int tmp = 0;
        char[] restoreDNA = new char[10000000];
        char[] referenceDNA = new char[10000000];
        String sequence = "";
        ArrayList<Integer> findList = new ArrayList<Integer>();

        // index생성

        LinkedList<Integer>[] index = createIndex(FILEPATH);

        // 복원될 문자열의 빈부분을 '?'로 채우기
        for (i = 0; i < 10000000; i++) {
            restoreDNA[i] = '?';
        }


        // 비교에 이용할 "ReferenceDNA.txt" 입력받기
        // "ReferenceDNA.txt" 입력받기
        String temp_str = new String("");
        try {
            FileReader fr = new FileReader(FILEPATH);
            BufferedReader br = new BufferedReader(fr);

            while ((temp_str = br.readLine()) != null) {
                referenceDNA = temp_str.toCharArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // "MyDNAsequence.txt" 입력받기
        try {
            File file = new File("MyDNAsequence.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((sequence = bufferedReader.readLine()) != null) {
                // 입력받은 sequence를 10개씩 끊어서 처리
                for (j = 0; j < k - 10; j++) {
                    // sequence에서 끊은 10자리의 문자열의 indNum 계산
                    indNum = 0;
                    for (m = 0; m < 10; m++) {
                        switch (sequence.charAt(j + m)) {
                            case ('A'):
                                tmp = 0;
                                break;
                            case ('C'):
                                tmp = 1;
                                break;
                            case ('G'):
                                tmp = 2;
                                break;
                            case ('T'):
                                tmp = 3;
                                break;
                        }
                        indNum += tmp;
                        indNum *= 4;
                    }
                    indNum /= 4;

                    // index에 indNum의 포지션들이 기록되어 있다면 차례로 방문
                    if (index[indNum] != null) {
                        for (Integer iter : index[indNum]) { //for문을 통한 전체출력
                            findList.add(iter);
                        }
                    }
                }
                findList.sort(null);

                for (j = 0; j < findList.size(); j++) {
                    count = 0;
                    for (m = j + 1; m < findList.size(); m++) {
                        if (findList.get(j) + k > findList.get(m)) {
                            count++;
                        } else {
                            break;
                        }
                    }

                    if (count > 10) {
                        for (m = 0; m < 20; m++) {
                            // 스닙과 start위치, end위치 초기화
                            SNP = 0;
                            start = findList.get(j) - m;
                            end = start + k - 1;

                            // start와 end가 범위안에 있을때만 시행
                            if (start > 0 && end < 10000000) {
                                // refenceDNA와 sequence를 비교, 스닙이 4개보다 크게 나오면 비교 종료
                                // 비교가 끝난후 스닙이 4개 이하면 restoreDNA에 sequence 기록
                                for (h = 0; h < k; h++) {
                                    if (referenceDNA[start + h] != sequence.charAt(h)) {
                                        SNP++;
                                    }
                                    if (SNP > 4) {
                                        break;
                                    }
                                }
                                if (SNP <= 4) {
                                    for (h = 0; h < k; h++) {
                                        restoreDNA[start + h] = sequence.charAt(h);
                                    }
                                }

                            }
                        }
                    }
                }

                findList.clear();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // sequence의 내용을 위치에 맞게 기록한 restoreDNA를 "RestoreDNA.txt"로 출력
        // ReferenceDNA 출력
        File file = new File("RestoreDNA.txt");
        String strForFile = new String(restoreDNA);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(strForFile);
            writer.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }


    }
    /*
    void형 Accuracy 함수
    MyDNA와 RestoreDNA를 비교하여 정확도를 측정
    입력 :
        없음
    출력 :
        없음
    */
    static void Accuracy() {
        int i;
        int accuracy = 0;
        char[] myDNA = new char[str_Size+1];
        char[] restoreDNA = new char[str_Size+1];

        // "MyDNA.txt"입력받기
        String theString = new String("");
        try {
            File file = new File("myDNA.txt");
            Scanner scanner = new Scanner(file);

            theString = scanner.nextLine();
            while (scanner.hasNextLine()) {
                theString = theString + "\n" + scanner.nextLine();
            }

            myDNA = theString.toCharArray();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // "RestoreDNA.txt"입력받기
        theString = "";
        try {
            File file = new File("RestoreDNA.txt");
            Scanner scanner = new Scanner(file);

            theString = scanner.nextLine();
            while (scanner.hasNextLine()) {
                theString = theString + "\n" + scanner.nextLine();
            }

            restoreDNA = theString.toCharArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // "MyDNA.txt"와 "RestoreDNA.txt"를 하나씩 비교하여 같은부분이 있으면 accuracy를 1증가
        for (i = 0; i < str_Size; i++) {
            if (myDNA[i] == restoreDNA[i]) {
                accuracy++;
            }
        }

        // 일치율 출력
        System.out.println("일치율 : ");
        System.out.println(accuracy + " / "+str_Size);
    }

    public static void main(String[] args) {
        int k, n;
        Scanner sc = new Scanner(System.in);

        // k와 n 입력 
        System.out.print("k : ");
        k = sc.nextInt();
        System.out.print("n : ");
        n = sc.nextInt();

        // 읽을 파일
        String file_path = "ReferenceDNA.txt";

        
        createRefDNA(); // Reference DNA 생성
        getMyReference(file_path); // My Reference DNA 생성 
        makeSpectrum(k, n);        // ShortRead 생성


        // New Algorithm 알고리즘 시간 측정
        long beforeTime = System.currentTimeMillis(); // 코드 실행 전 시간
        NewAlgorithm(file_path, k, n);
        long afterTime = System.currentTimeMillis(); // 코드 실행 후 시간
        long secDiffTime = (afterTime - beforeTime) / 1000; //두 시간에 차 계산
        System.out.println("New Method 수행시간 : " + secDiffTime + "s");
        Accuracy();

        // trivial 알고리즘 시간 측정
        beforeTime = System.currentTimeMillis();
        Trivial(file_path, k, n);
        afterTime = System.currentTimeMillis();
        System.out.println("Trivial Method 수행시간 : " + secDiffTime + "s");
        Accuracy();
    }
}