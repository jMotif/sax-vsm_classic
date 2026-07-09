package net.seninp.jmotif.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a random strategy
 * 
 * @author psenin
 * 
 */
public class RandomStartStrategy implements StartStrategy {

  // logger business
  private static final Logger consoleLogger = LoggerFactory.getLogger(RandomStartStrategy.class);

  /**
   * {@inheritDoc}
   */
  @Override
  public LinkedHashMap<String, HashMap<String, Double>> getCentroids(Integer num,
      HashMap<String, HashMap<String, Double>> data) {

    Random random = new Random();

    LinkedHashMap<String, HashMap<String, Double>> res = new LinkedHashMap<String, HashMap<String, Double>>();

    ArrayList<String> keys = new ArrayList<String>();
    for (String k : data.keySet()) {
      keys.add(k.substring(0));
    }

    for (int i = 0; i < num; i++) {

      int rand = random.nextInt(keys.size());
      String key = keys.get(rand).substring(0);
      keys.remove(rand);
      consoleLogger.info("random cluster " + i + ", center: " + key);

      HashMap<String, Double> value = new HashMap<String, Double>();
      for (Entry<String, Double> e : data.get(key).entrySet()) {
        value.put(e.getKey().substring(0), Double.valueOf(e.getValue()));
      }

      res.put(String.valueOf(i), value);

    }
    return res;
  }

}
