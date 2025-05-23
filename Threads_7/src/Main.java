/*class ParkingLot {
    private final int totalSpaces;
    private int usedSpaces = 0;

    public ParkingLot(int totalSpaces) {
        this.totalSpaces = totalSpaces;
    }

    public synchronized void parkCar(String carName) {
        while (usedSpaces >= totalSpaces) {
            try {
                System.out.println(carName + " ожидает парковку...");
                wait(); // Ожидание, если нет доступных мест
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(carName + " был прерван.");
            }
        }
        usedSpaces++;
        System.out.println(carName + " припарковался. Занято мест: " + usedSpaces);
    }

    public synchronized void leaveCar(String carName) {
        usedSpaces--;
        System.out.println(carName + " уехал. Занято мест: " + usedSpaces);
        notifyAll(); // Уведомляем другие потоки о доступных местах
    }
}

class Car extends Thread {
    private final ParkingLot parkingLot;
    private final String carName;

    public Car(ParkingLot parkingLot, String carName) {
        this.parkingLot = parkingLot;
        this.carName = carName;
    }

    @Override
    public void run() {
        parkingLot.parkCar(carName);
        try {
            // Имитируем время парковки
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            parkingLot.leaveCar(carName);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLot(3); // 3 парковочных места

        // Создаем и запускаем 10 машин
        for (int i = 1; i <= 10; i++) {
            Car car = new Car(parkingLot, "Машина " + i);
            car.start();
        }
    }
}*/


/*import java.util.concurrent.Semaphore;

class ParkingLot {
    private final Semaphore parkingSpaces;

    public ParkingLot(int numberOfSpaces) {
        this.parkingSpaces = new Semaphore(numberOfSpaces);
    }

    public void parkCar(String carName) {
        try {
            System.out.println(carName + " пытается припарковаться.");
            parkingSpaces.acquire();
            System.out.println(carName + " припарковался.");
            Thread.sleep(2000); // Симуляция времени парковки
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println(carName + " выезжает.");
            parkingSpaces.release();
        }
    }
}

class Car extends Thread {
    private final ParkingLot parkingLot;

    public Car(ParkingLot parkingLot, String name) {
        super(name);
        this.parkingLot = parkingLot;
    }

    @Override
    public void run() {
        parkingLot.parkCar(getName());
    }
}

public class Main {
    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLot(3); // 3 парковочных места

        // Создаем 10 машин
        for (int i = 1; i <= 10; i++) {
            Car car = new Car(parkingLot, "Машина " + i);
            car.start();
        }
    }
}*/

/*import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class ParkingLot {
    private final int totalSpaces;
    private int availableSpaces;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition spaceAvailable = lock.newCondition();

    public ParkingLot(int totalSpaces) {
        this.totalSpaces = totalSpaces;
        this.availableSpaces = totalSpaces;
    }

    public void parkCar(String carName) throws InterruptedException {
        lock.lock();
        try {
            while (availableSpaces == 0) {
                System.out.println(carName + " ждет свободное место.");
                spaceAvailable.await();
            }
            availableSpaces--;
            System.out.println(carName + " припарковался. Свободные места: " + availableSpaces);
        } finally {
            lock.unlock();
        }
    }

    public void leaveCar(String carName) {
        lock.lock();
        try {
            availableSpaces++;
            System.out.println(carName + " уехал. Свободные места: " + availableSpaces);
            spaceAvailable.signal();
        } finally {
            lock.unlock();
        }
    }
}

class Car extends Thread {
    private final ParkingLot parkingLot;

    public Car(ParkingLot parkingLot, String name) {
        super(name);
        this.parkingLot = parkingLot;
    }

    @Override
    public void run() {
        try {
            parkingLot.parkCar(getName());
            Thread.sleep(1000); // Симуляция времени парковки
            parkingLot.leaveCar(getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLot(3); // 3 парковочных места

        for (int i = 1; i <= 10; i++) {
            new Car(parkingLot, "Машина " + i).start();
        }
    }
}
*/

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class ParkingLot {
    private final Semaphore spaces;

    public ParkingLot(int totalSpaces) {
        this.spaces = new Semaphore(totalSpaces);
    }

    public boolean parkCar(String carName) {
        try {
            // Попытка занять место на определённое время
            if (spaces.tryAcquire(2, TimeUnit.SECONDS)) {
                System.out.println(carName + " parked.");
                return true;
            } else {
                System.out.println(carName + " could not find a parking space and left.");
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void leaveCar(String carName) {
        spaces.release();
        System.out.println(carName + " has left the parking space.");
    }
}

class Car implements Runnable {
    private final ParkingLot parkingLot;
    private final String name;

    public Car(ParkingLot parkingLot, String name) {
        this.parkingLot = parkingLot;
        this.name = name;
    }

    @Override
    public void run() {
        if (parkingLot.parkCar(name)) {
            try {
                // Симуляция времени стоянки
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                parkingLot.leaveCar(name);
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLot(3); // 3 машиноместа

        for (int i = 1; i <= 10; i++) {
            Car car = new Car(parkingLot, "Car " + i);
            new Thread(car).start();
        }
    }
}