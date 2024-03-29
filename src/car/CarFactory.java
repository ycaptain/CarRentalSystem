package car;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class CarFactory implements Car {
	/**
	 * Small car type.
	 */
	public static final String SMALL_CAR = "small";
	/**
	 * Large car type.
	 */
	public static final String LARGE_CAR = "large";

	// map of car registration number to instantiated car
	private static final Map<String, Car> cars = new HashMap<String, Car>();

	private final RegistrationNumber regisNum;
	private final int capacity;
	private int fuel;
	private boolean isRented;

	CarFactory(RegistrationNumber regisNum, int capacity) {
		this.regisNum = RegistrationNumber.valueOf(regisNum.toString());
		this.capacity = capacity;
		this.fuel = capacity;
		this.isRented = false;
	}

	CarFactory(RegistrationNumber regisNum, int capacity, int fuel, boolean isRented) {
		this.regisNum = RegistrationNumber.valueOf(regisNum.toString());
		this.capacity = capacity;
		this.fuel = fuel;
		this.isRented = isRented;
	}

	/**
	 * Returns a car of the specified type with the specified registration number.
	 *
	 * @param carType
	 *            the type of car to return
	 * @param regisNum
	 *            the registration number
	 * @return a car of the specified type. An existing car is returned if regisNum
	 *         is already known. Otherwise a new car with the given number is
	 *         returned.
	 * @throws NullPointerException
	 *             if carType is null
	 * @throws IllegalArgumentException
	 *             if carType is an invalid carType
	 */
	public static Car getInstance(String carType, RegistrationNumber regisNum) {
		// enforce single instance per registration number
		final String rn = regisNum.toString();
		Car car = cars.get(rn);
		if (car != null)
			return valueOf(car.toString());

		// return either small or large car.
		if (carType.equals(SMALL_CAR)) {
			car = new SmallCar(regisNum);
		} else if (carType.equals(LARGE_CAR)) {
			car = new LargeCar(regisNum);
		} else {
			throw new IllegalArgumentException("invalid car type: " + carType);
		}

		// put car in cars map
		cars.put(rn, car);

		// return the instance
		return valueOf(car.toString());
	}

	/**
	 * @see car.Car#regisNum()
	 */
	@Override
	public RegistrationNumber regisNum() {
		return RegistrationNumber.valueOf(regisNum.toString());
	}

	/**
	 * @see car.Car#capacity()
	 */
	@Override
	public int capacity() {
		return capacity;
	}

	/**
	 * @see car.Car#fuel()
	 */
	@Override
	public int fuel() {
		return fuel;
	}

	/**
	 * @see car.Car#isFull()
	 */
	@Override
	public boolean isFull() {
		return fuel == capacity;
	}

	/**
	 * @see car.Car#isRented()
	 */
	@Override
	public boolean isRented() {
		return isRented;
	}

	/**
	 * @see car.Car#refuel(int)
	 */
	@Override
	public int refuel(int nfuel) {
		if (nfuel < 0)
			throw new IllegalArgumentException("Negative refuel: " + nfuel);
		if (fuel == capacity)
			return 0;
		setFuel(fuel + nfuel);
		return nfuel + fuel <= capacity ? nfuel : capacity - fuel;
	}

	@Override
	public String toString() {
		return regisNum + "-" + capacity + "-" + fuel + "-" + isRented;
	}

	/**
	 * Constructs an instance of Car from its string representation.
	 *
	 * @param str
	 *            string representation of car
	 * @return an instance of Car from its string representation.
	 * @throws NullPointerException
	 *             if <code>str</code> is null
	 * @throws ArrayIndexOutOfBoundsException
	 *             if there are not two component parts to <code>str</code>
	 */
	public static Car valueOf(String str) {
		String[] parts = str.split("-");
		if (SmallCar.CAP == Integer.parseInt(parts[1]))
			return new SmallCar(RegistrationNumber.valueOf(parts[0]), Integer.parseInt(parts[2]),
					Boolean.parseBoolean(parts[3]));
		else if (LargeCar.CAP == Integer.parseInt(parts[1]))
			return new LargeCar(RegistrationNumber.valueOf(parts[0]), Integer.parseInt(parts[2]),
					Boolean.parseBoolean(parts[3]));
		else
			throw new IllegalArgumentException("invalid string representation of car: " + str);
	}

	// utility method to allow subclasses to set the car fuel
	void setFuel(int fuel) {
		this.fuel = fuel;
	}

	/**
	 * @see car.Car#issue()
	 */
	@Override
	public boolean issue() {
		if (isRented)
			return false;
		isRented = true;
		return true;
	}

	/**
	 * Returns whether issue operation is success or not.
	 *
	 * @return whether issue operation is success or not.
	 */
	public static boolean issue(Car car) {
		final Iterator<Car> cIter = cars.values().iterator();
		while (cIter.hasNext()) {
			final Car c = cIter.next();
			if (c.regisNum().equals(car.regisNum())) {
				c.issue();
				cars.put(c.regisNum().toString(), c);
				return true;
			}
		}
		return false;
	}

	/**
	 * @see car.Car#terminateRental()
	 */
	@Override
	public boolean terminateRental() {
		if (!isRented)
			return false;
		isRented = false;
		return true;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof Car))
			return false;

		final CarFactory cf = (CarFactory) obj;

		return cf.capacity == capacity && cf.regisNum.equals(regisNum);
	}

	/**
	 * Returns whether issue operation is success or not.
	 *
	 * @return whether issue operation is success or not.
	 */
	public static boolean terminateRental(Car car) {
		final Iterator<Car> cIter = cars.values().iterator();
		while (cIter.hasNext()) {
			final Car c = cIter.next();
			if (c.regisNum().equals(car.regisNum())) {
				c.terminateRental();
				cars.put(c.regisNum().toString(), c);
				return true;
			}
		}
		return false;
	}

	/**
	 * Update car map
	 * @param car
	 */
	void update(Car car) {
		cars.put(car.regisNum().toString(), car);
	}

	/**
	 * clear car map
	 */
	public static void clear() {
		cars.clear();
	}

	/**
	 * Returns the number of cars of the specified type that are available to rent.
	 *
	 * @param carType
	 *            the type of car
	 * @return the number of cars of the specified type that are available to rent.
	 * @throws IllegalArgumentException
	 *             if <code>carType</code> is a invalid car type.
	 */
	public static int availableCars(String carType) {
		boolean small;
		if (carType.equals(SMALL_CAR)) {
			small = true;
		} else if (carType.equals(LARGE_CAR)) {
			small = false;
		} else {
			throw new IllegalArgumentException("invalid car type: " + carType);
		}
		final Iterator<Car> cIter = cars.values().iterator();
		int count = 0;
		if (small) {
			while (cIter.hasNext()) {
				final Car car = cIter.next();
				if (car instanceof SmallCar && !car.isRented()) {
					count++;
				}
			}
		} else {
			while (cIter.hasNext()) {
				final Car car = cIter.next();
				if (car instanceof LargeCar && !car.isRented()) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Returns a available car by given car type.
	 *
	 * @param carType
	 *            car type.
	 * @return a available car by given car type.
	 */
	public static Car getCarByType(String carType) {
		boolean small;
		if (carType.equals(SMALL_CAR)) {
			small = true;
		} else if (carType.equals(LARGE_CAR)) {
			small = false;
		} else {
			throw new IllegalArgumentException("invalid car type: " + carType);
		}
		final Iterator<Car> cIter = cars.values().iterator();
		if (small) {
			while (cIter.hasNext()) {
				final Car car = cIter.next();
				if (car instanceof SmallCar && !car.isRented()) {
					return valueOf(car.toString());
				}
			}
		} else {
			while (cIter.hasNext()) {
				final Car car = cIter.next();
				if (car instanceof LargeCar && !car.isRented()) {
					return valueOf(car.toString());
				}
			}
		}
		return null;
	}

}
