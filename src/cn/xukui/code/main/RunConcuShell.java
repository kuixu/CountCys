package cn.xukui.code.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RunConcuShell {

	public static void main(String[] args) {
		// 1.#### Parameter configuration
		if (args.length < 2 || (args.length == 1 && args[0].equals("-h"))) {
			System.out.println("");
			System.out.println("Concu   : Count Cys in fasta file.");
			System.out.println("Version : 0.0.1");
			System.out.println("Usage   : java -jar countcys.jar -f test.fasta");
			System.out.println(" -f  <str>    Protein fasta file.");
			System.out.println("");
			System.out.println("---");
			//System.out.println(" -t  <int>    Number of concurrent threads [1, ~]; ");
			//System.out.println(" -w  <str>    Daemon name to be watched. eg.'java','wget'.");
			System.out.println(" -v           View output.");
			System.out.println(" -h           View help option.");
			System.out.println("");
			System.out.println("");
			System.exit(1);
		}

		String file = "";
		boolean isOutput = false;

		int i = 0;
		while (i < args.length) {
			switch (args[i]) {
			case "-f":
				file = args[i + 1];
				//System.out.println("Commandline list file :" + file);
				File f = new File(file);
				if (!f.exists()) {
					System.out.println("file not found:" + file);
					return;
				}
				i += 2;
				break;
			case "-v":
				isOutput = true;
				i++;
				// variation = double.Parse(args[i + 1]);
				break;
			
			default:
				System.out.println("no option:" + args[i]);
				return;

			}
		}

		if (!"".equals(file)) {
			CalcC(file,isOutput);
		}else {
			System.out.println("no input fasta file");
		}

	}

	

	
	public static void CalcC(String readpath,boolean isOutput){
		List<String> lst=new ArrayList<String>();
		//String readpath="/Users/xukui/Downloads/10000-fasta.txt";
		BufferedReader bReader = null;
		try {
			bReader = new BufferedReader(new FileReader(new File(readpath)));
		//	int i = -1;
			String line = "";
			String head = "";
			StringBuilder contigsb = new StringBuilder();
			while ((line = bReader.readLine()) != null) {

				// ##### ATTENTION : if and else share the code
				if (line.startsWith(">") ) {
					//i++;// line number times
					
					if (!contigsb.toString().isEmpty()) {
						String []headArr=head.split("\\|");
						int c=countC(contigsb.toString());
						lst.add(headArr[3]+"\t"+c);
						if (isOutput) {
							System.out.println(headArr[3]+"\t"+c);	
						}
						contigsb.delete(0, contigsb.length());
					}// process one read
						head=line;
				} else {
					// if (i >= startline) {
					contigsb.append(line.toUpperCase());
					// }
				}
			}// while
				// the last contig change should the with >>>>>>>>>>>>>>>>
			if (!contigsb.toString().isEmpty()) {
				String []headArr=head.split("\\|");
				int c=countC(contigsb.toString());
				lst.add(headArr[3]+"\t"+c);
				if (isOutput) {
					System.out.println(headArr[3]+"\t"+c);	
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// ###why ? threads
				bReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		WriteList2File(lst, readpath+"-cys.txt");
		System.out.println(lst.size()+" lines processed, and saved into file:"+readpath+"-cys.txt");

	}

	private static int countC(String string) {
		int sum=0;
		char[]arr=string.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			sum+=arr[i]=='C'?1:0;
		}
		return sum;
	}
	public static void WriteList2File(List<String> contentList, String filename) {

		File result_file = new File(filename);
		try {
			File dire = new File(result_file.getParent());
			if (!dire.isDirectory()) {
				dire.mkdirs();
			}
			result_file.createNewFile();
			BufferedWriter output = new BufferedWriter(new FileWriter(result_file));
			for (String content : contentList) {
				output.write(content + "\n");
			}

			output.close();

		} catch (IOException e) {
			System.err.println(result_file + " file write error!");
		}

	}

	
	
	public static List<String> GetFileContentList(String filepath) {
		List<String> fileContentList = new ArrayList<String>();
		File file = new File(filepath);
		if (!file.isFile())
			return null;
		
		BufferedReader bReader;
		try {
			bReader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = bReader.readLine()) != null) {
				fileContentList.add(line);
			}
			bReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fileContentList = null;
		} catch (IOException e) {
			e.printStackTrace();
			fileContentList = null;
		}

		return fileContentList;
	}

}
