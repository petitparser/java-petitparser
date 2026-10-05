package org.petitparser.benchmark;

import java.util.Objects;

/**
 * Encapsulates the execution measurements of a benchmark run.
 */
public class BenchmarkResult {

  private final String name;
  private final int iterations;
  private final long elapsedNanos;
  private final long allocatedBytes;

  public BenchmarkResult(String name, int iterations, long elapsedNanos, long allocatedBytes) {
    this.name = Objects.requireNonNull(name, "name must not be null");
    this.iterations = iterations;
    this.elapsedNanos = elapsedNanos;
    this.allocatedBytes = Math.max(0, allocatedBytes);
  }

  public String getName() {
    return name;
  }

  public int getIterations() {
    return iterations;
  }

  public long getElapsedNanos() {
    return elapsedNanos;
  }

  public long getAllocatedBytes() {
    return allocatedBytes;
  }

  public double getOperationsPerSecond() {
    return elapsedNanos > 0 ? (iterations * 1_000_000_000.0) / elapsedNanos : 0.0;
  }

  public double getNanosPerOperation() {
    return iterations > 0 ? (double) elapsedNanos / iterations : 0.0;
  }

  public double getBytesPerOperation() {
    return iterations > 0 ? (double) allocatedBytes / iterations : 0.0;
  }

  @Override
  public String toString() {
    return String.format("%-42s %12.2f ops/s  %9.2f ns/op  %9.2f B/op",
        name, getOperationsPerSecond(), getNanosPerOperation(), getBytesPerOperation());
  }
}
