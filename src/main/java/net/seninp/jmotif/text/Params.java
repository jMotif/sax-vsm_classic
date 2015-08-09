package net.seninp.jmotif.text;

import net.seninp.jmotif.sax.NumerosityReductionStrategy;

/**
 * Parameters for sax-vsm transform.
 * 
 * @author psenin
 * 
 */
public class Params {

  protected int windowSize;
  protected int paaSize;
  protected int alphabetSize;
  protected double nThreshold;
  protected NumerosityReductionStrategy nrStartegy;
  protected double cvError;

  public Params(int windowSize, int paaSize, int alphabetSize, double nThreshold,
      NumerosityReductionStrategy nrStartegy, double cvError) {
    super();
    this.windowSize = windowSize;
    this.paaSize = paaSize;
    this.alphabetSize = alphabetSize;
    this.nThreshold = nThreshold;
    this.nrStartegy = nrStartegy;
    this.cvError = cvError;
  }

  public Params(int windowSize, int paaSize, int alphabetSize, double nThreshold,
      NumerosityReductionStrategy nrStartegy) {
    super();
    this.windowSize = windowSize;
    this.paaSize = paaSize;
    this.alphabetSize = alphabetSize;
    this.nThreshold = nThreshold;
    this.nrStartegy = nrStartegy;
    this.cvError = Double.NaN;
  }

  public int getWindowSize() {
    return windowSize;
  }

  public void setWindowSize(int windowSize) {
    this.windowSize = windowSize;
  }

  public int getPaaSize() {
    return paaSize;
  }

  public void setPaaSize(int paaSize) {
    this.paaSize = paaSize;
  }

  public int getAlphabetSize() {
    return alphabetSize;
  }

  public void setAlphabetSize(int alphabetSize) {
    this.alphabetSize = alphabetSize;
  }

  public double getnThreshold() {
    return nThreshold;
  }

  public void setnThreshold(double nThreshold) {
    this.nThreshold = nThreshold;
  }

  public NumerosityReductionStrategy getNrStartegy() {
    return nrStartegy;
  }

  public void setNrStartegy(NumerosityReductionStrategy nrStartegy) {
    this.nrStartegy = nrStartegy;
  }

  public double getCvError() {
    return cvError;
  }

  public void setCvError(double cvError) {
    this.cvError = cvError;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + alphabetSize;
    long temp;
    temp = Double.doubleToLongBits(cvError);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(nThreshold);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((nrStartegy == null) ? 0 : nrStartegy.hashCode());
    result = prime * result + paaSize;
    result = prime * result + windowSize;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Params other = (Params) obj;
    if (alphabetSize != other.alphabetSize)
      return false;
    if (Double.doubleToLongBits(cvError) != Double.doubleToLongBits(other.cvError))
      return false;
    if (Double.doubleToLongBits(nThreshold) != Double.doubleToLongBits(other.nThreshold))
      return false;
    if (nrStartegy != other.nrStartegy)
      return false;
    if (paaSize != other.paaSize)
      return false;
    if (windowSize != other.windowSize)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Params [windowSize=" + windowSize + ", paaSize=" + paaSize + ", alphabetSize="
        + alphabetSize + ", nThreshold=" + nThreshold + ", nrStartegy=" + nrStartegy + ", cvError="
        + cvError + "]";
  }

}
