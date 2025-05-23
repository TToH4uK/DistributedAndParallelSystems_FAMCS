import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class MatrixVectorMultiplication {
    static class MultiplyTask extends RecursiveTask<double[]> {
        private final double[][] matrix;
        private final double[] vector;
        private final int startRow;
        private final int endRow;

        public MultiplyTask(double[][] matrix, double[] vector, int startRow, int endRow) {
            this.matrix = matrix;
            this.vector = vector;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        protected double[] compute() {
            int numRows = endRow - startRow;
            if (numRows == 1) {
                return new double[]{multiplyRow(matrix[startRow], vector)};
            } else {
                int mid = (startRow + endRow) / 2;
                MultiplyTask task1 = new MultiplyTask(matrix, vector, startRow, mid);
                MultiplyTask task2 = new MultiplyTask(matrix, vector, mid, endRow);
                invokeAll(task1, task2);
                double[] result1 = task1.join();
                double[] result2 = task2.join();
                double[] result = new double[numRows];
                System.arraycopy(result1, 0, result, 0, result1.length);
                System.arraycopy(result2, 0, result, result1.length, result2.length);
                return result;
            }
        }

        private double multiplyRow(double[] row, double[] vector) {
            double sum = 0;
            for (int i = 0; i < row.length; i++) {
                sum += row[i] * vector[i];
            }
            return sum;
        }
    }

    public static double[] multiply(double[][] matrix, double[] vector) {
        ForkJoinPool pool = new ForkJoinPool();
        return pool.invoke(new MultiplyTask(matrix, vector, 0, matrix.length));
    }

    public static void main(String[] args) {
        double[][] matrix = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        double[] vector = {-9, 5, 3};
        double[] result = multiply(matrix, vector);
        for (double value : result) {
            System.out.println(value);
        }
    }
}