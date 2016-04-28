package net.seninp.jmotif.direct;

import java.util.ArrayList;
import java.util.List;
import com.beust.jcommander.Parameter;

/**
 * This implements a classifier.
 * 
 * The direct code was taken from JCOOL (Java COntinuous Optimization Library), and altered for
 * SAX-VSM needs.
 * 
 * @see <a href="https://github.com/dhonza/JCOOL/wiki">https://github.com/dhonza/JCOOL/wiki</a>
 * 
 */
public class SAXVSMDirectSamplerParams {

  //
  // -train src/resources/data/cbf/CBF_TRAIN -test src/resources/data/cbf/CBF_TEST -wmin 10 -wmax
  // 120 -pmin 3 -pmax 10 -amin 2 -amax 10 --hold_out 1 -i 3
  //

  // general setup
  //
  @Parameter
  public List<String> parameters = new ArrayList<>();

  // datasets
  //
  @Parameter(names = { "--train_data", "-train" }, description = "The input file name")
  public static String TRAIN_FILE;

  @Parameter(names = { "--test_data", "-test" }, description = "The input file name")
  public static String TEST_FILE;

  // discretization parameter ranges
  //
  @Parameter(names = { "--window_size_min", "-wmin" }, description = "min SAX sliding window size")
  public static int SAX_WINDOW_SIZE_MIN = 10;

  @Parameter(names = { "--window_size_max", "-wmax" }, description = "max SAX sliding window size")
  public static int SAX_WINDOW_SIZE_MAX = 100;

  @Parameter(names = { "--word_size_min", "-pmin" }, description = "min SAX PAA word size")
  public static int SAX_PAA_SIZE_MIN = 3;

  @Parameter(names = { "--word_size_max", "-pmax" }, description = "max SAX PAA word size")
  public static int SAX_PAA_SIZE_MAX = 10;

  @Parameter(names = { "--alphabet_size_min", "-amin" }, description = "min SAX alphabet size")
  public static int SAX_ALPHABET_SIZE_MIN = 3;

  @Parameter(names = { "--alphabet_size_max", "-amax" }, description = "max SAX alphabet size")
  public static int SAX_ALPHABET_SIZE_MAX = 5;

  @Parameter(names = "--threshold", description = "SAX normalization threshold")
  public static double SAX_NORM_THRESHOLD = 0.01;

  @Parameter(names = "--hold_out", description = "CV hold out number")
  public static int HOLD_OUT_NUM = 1;

  @Parameter(names = { "--iter", "-i" }, description = "max allowed iterations")
  public static int ITERATIONS_NUM = 1;

  @Parameter(names = { "--break", "-b" }, description = "max allowed iterations")
  public static double ITERATIONS_BREAK_THRESHOLD = 0.001;

}
