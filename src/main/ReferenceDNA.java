package main;

import editReference.InputReference;

import java.io.*;
import java.util.*;

public class ReferenceDNA {

    public static int file_size;

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
        char[] referenceDNA = new char[file_size]; // Reference DNA
        int reference_len;                        // referenceDNA length
        char[] restoreDNA = new char[file_size];   // 복원 후 문자열
        
        // 복원될 문자열의 빈부분을 '?'로 채우기
        Arrays.fill(restoreDNA,'?');

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
        reference_len = referenceDNA.length;

        // "MyDNAsequence.txt" 입력받기
        String sequence = "";
        try {
            File file = new File("refer/MyDNAsequence.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((sequence = bufferedReader.readLine()) != null) {    // 한 줄씩 입력받음
            	for(j = 0; j < reference_len - k + 1; j++) {  // reference DNA 순회
            		SNP = 0; // miss match 0으로 set
            		// 문자열 비교
            		for(m = 0; m < k; m++) {
            			if(referenceDNA[j + m]!=sequence.charAt(m)) {  // 다른 문자 check
            				SNP++;
                            // 스닙이 4개 보다 더 나오면 불일치이므로 바로 break
                            if(SNP > 3) {
                                break;
                            }
            			}

            		}
            		// reference와 shortRead 비교 이후에도 스닙이 4개 이하일때 restoreDNA에 값 저장
            		if (SNP <= 3) {
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


        // sequence의 내용을 위치에 맞게 기록한 restoreDNA를 "trivialRestoreDNA.txt"로 출력
        File file = new File("output/trivialRestoreDNA.txt");
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
        char[] referenceDNA = new char[file_size]; // Reference DNA
        int reference_len;                        // referenceDNA length
        int[] forIndex = new int[file_size];
        int partShort_len = 10; // 부분 short 길이

        // 인덱스의 크기인 size = 4^partShort_len
        for (i = 0; i < partShort_len; i++) {
            size *= 4;
        }

        //인덱스는 인접리스트 방식으로 구현
        LinkedList<Integer> indexNode[] = new LinkedList[size];

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
        reference_len = referenceDNA.length;


        // 문자열을 계산이 쉽도록 int형 배열로 변환
        for (i = 0; i < reference_len; i++) {
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
        for (i = 0; i < partShort_len; i++) {
            indNum *= 4;
            indNum += forIndex[i];
        }

        //처음의 10자리의 위치 indexNode에 넣기
        indexNode[indNum] = new LinkedList<Integer>();
        indexNode[indNum].add(0);


        D = size / 4;
        for (i = 1; i < reference_len - partShort_len + 1; i++) {
            // 라빈카프 알고리즘의 방식처럼 10자리의 위치를 모두 구하는것이 아닌
            // 연산을 통해 밀려난것을 빼주고, 자릿수를 고쳐주고, 새롭게 들어오는 부분을 더해주는 방식으로 indNum을 계산
            indNum = (indNum - forIndex[i - 1] * D) * 4 + forIndex[i + (partShort_len-1)];

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
        int j, m, h, indNum, SNP, count, start, end;
        int tmp = 0;
        char[] referenceDNA = new char[file_size];  // Reference DNA
        int reference_len;                          // referenceDNA length
        char[] restoreDNA = new char[file_size];    // 복원 후 문자열
        String sequence = ""; // 입력받은 한 줄을 저장할 String 변수
        ArrayList<Integer> findList = new ArrayList<Integer>();  // referenceDNA에서 sequence의 일부와 동일한 hash 값을 갖는
                                                                 // index 저장



        LinkedList<Integer>[] index = createIndex(FILEPATH); // 각 hash 값을 갖는 부분 문자열의 시작 위치 저장

        // 복원될 문자열의 빈부분을 '?'로 채우기
        Arrays.fill(restoreDNA,'?');

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
        reference_len = referenceDNA.length;


        // "MyDNAsequence.txt" 입력받기
        try {
            File file = new File("refer/MyDNAsequence.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((sequence = bufferedReader.readLine()) != null) {
                // 입력받은 sequence를 10개씩 끊어서 처리
                for (j = 0; j < k - 10 + 1; j+=3) {
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
                        for (Integer iter : index[indNum]) {
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
                            if (start > 0 && end < reference_len) {
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
        File file = new File("output/NewRestoreDNA.txt");
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
    MyDNA와 RestoreDNA(restore_file_path)를 비교하여 정확도를 측정
    입력 :
        restore_file_path = 알고리즘으로 복원된 file 경로
    출력 :
        없음
    */
    static void Accuracy(String restore_file_path) {
        int i;
        int accuracy = 0;
        char[] myDNA = new char[file_size+1];
        int myDNA_len;
        char[] restoreDNA = new char[file_size+1];

        // "MyDNA.txt"입력받기
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
        myDNA_len = myDNA.length;


        // RestoreDNA 입력받기
        stringBuilder.setLength(0);
        temp_str = "";
        try {
            FileReader fr = new FileReader(restore_file_path);
            BufferedReader br = new BufferedReader(fr);

            while ((temp_str = br.readLine()) != null) { // read line from file
                stringBuilder.append(temp_str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        restoreDNA = stringBuilder.toString().toCharArray(); //  convert StringBuilder to char array


        // "MyDNA.txt"와 "RestoreDNA.txt"를 하나씩 비교하여 같은부분이 있으면 accuracy를 1증가
        for (i = 0; i < myDNA_len; i++) {
            if (myDNA[i] == restoreDNA[i]) {
                accuracy++;
            }
        }

        // 일치율 출력
        System.out.println("일치율 : ");
        System.out.println(accuracy + " / "+myDNA_len);
    }

    public static void main(String[] args) {
        int k, n;
        Scanner sc = new Scanner(System.in);

        // k와 n 입력 
        System.out.println("k : 70");
        //k = sc.nextInt();
        k = 70;

        System.out.println("n : 450000");
        //n = sc.nextInt();
        n = 40000;


        // 읽을 파일
        /*String file_path = "input/Original.txt";
        try { // get the size of input file
            File file = new File(file_path);
            file_size= (int)file.length();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        file_size= 1000000;
        String file_path= "input/ReferenceDNA.txt";

        InputReference.createRefDNA(); // Reference DNA 생성
        InputReference.getMyReference(file_path); // My Reference DNA 생성
        InputReference.makeSpectrum(k, n);        // ShortRead 생성


        // New Algorithm 알고리즘 시간 측정
        long beforeTime = System.currentTimeMillis(); // 코드 실행 전 시간
        NewAlgorithm(file_path, k, n);
        long afterTime = System.currentTimeMillis(); // 코드 실행 후 시간
        long secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
        System.out.println("New Method 수행시간 : " + secDiffTime + "ms");
        Accuracy("output/NewRestoreDNA.txt");

        // trivial 알고리즘 시간 측정
        beforeTime = System.currentTimeMillis();
        Trivial(file_path, k, n);
        afterTime = System.currentTimeMillis();
        secDiffTime = (afterTime - beforeTime); //두 시간에 차 계산
        System.out.println("Trivial Method 수행시간 : " + secDiffTime + "ms");
        Accuracy("output/trivialRestoreDNA.txt");
    }
}