package pw.edu.elka.rso.logic.beans;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Vector;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: Maciek
 * Date: 25.05.13
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */
public class FileReader {

  final static Charset ENCODING = StandardCharsets.UTF_8;
  Vector<String> queries = new Vector<String>();


  /**  Laduje do vectora stringow wiersze z pliku podanego jako argument metody.
   *
   * @param inputFileName
   * @return String
   * @throws IOException
   */
  public Vector<String> readFile(String inputFileName) throws IOException{
    Path inputPath = Paths.get(inputFileName);
    Scanner scanner = null;
    //       Vector<String> queries = null;
    try{
      scanner = new Scanner(inputPath, ENCODING.name());
      while(scanner.hasNextLine()){
        String currLine = new String(scanner.nextLine().getBytes(),"UTF-8");
        queries.add(currLine);
      }
    }
    catch (IOException e){
      e.printStackTrace();
    }
    finally{
      scanner.close();
    }
    return queries;
  }

  /**
   *
   * @param i
   * @return
   */
  public String getElement(int i){
    return queries.get(i);
  }

  /**
   *
   * @return
   */
  public int getSize(){
    return queries.size();
  }
}