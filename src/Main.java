import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MultiThreadCalculator calculator = new MultiThreadCalculator();
        calculator.Start();
    }
}

class MultiThreadCalculator{
    private long MultiThreadSum = 0;
    private  long ThreadCounter = 0;

    private  int[] calculateArray;

    public void Start(){
        Scanner input = new Scanner(System.in);

        System.out.print("Input a array size: ");
        int arraySize = input.nextInt();
        calculateArray = new int[arraySize];
        for (int i = 0; i < arraySize; i++){
            calculateArray[i] = i;
        }

        int arrayElementsSum = 0;
        for (int i = 0; i<arraySize;i++){
            arrayElementsSum+=calculateArray[i];
        }

        System.out.println("One thread sum: "+ arrayElementsSum);

        int endIndex = arraySize - 1;
        while (endIndex!=0){
            int maxThreads = arraySize/2;
            ThreadCounter = 0;
            int startIndex = 0;
            while(endIndex>startIndex){
                Thread newThread = new Thread(new CalculatorThread(this, calculateArray, startIndex, endIndex));
                newThread.start();
                startIndex++;
                endIndex--;
            }

            synchronized (this) {
                while (ThreadCounter < maxThreads) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if(arraySize % 2 > 0)
                arraySize = arraySize/2 + 1;
            else
                arraySize /= 2;
            endIndex = arraySize - 1;
        }

        System.out.println("Multiply thread sum: " + calculateArray[0]);
    }

    public synchronized  void SetSumOfElements(int sum, int index){
        calculateArray[index] = sum;
        ThreadCounter +=1;
        notify();
    }
}

class CalculatorThread implements Runnable{

    int[] ThisArray;
    int firstIndex, secondIndex;
    MultiThreadCalculator calculatorRef;

    CalculatorThread(MultiThreadCalculator calculator, int[] array, int firstIndex, int secondIndex) {
        this.firstIndex = firstIndex;
        this.secondIndex = secondIndex;
        ThisArray = array;
        calculatorRef = calculator;
    }

    @Override
    public void run() {
        int sum = ThisArray[firstIndex]+ThisArray[secondIndex];
        calculatorRef.SetSumOfElements(sum, firstIndex);
    }
}