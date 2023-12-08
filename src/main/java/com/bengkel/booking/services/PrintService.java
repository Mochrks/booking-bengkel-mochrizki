package com.bengkel.booking.services;

import java.util.List;

import com.bengkel.booking.models.Car;
import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.Vehicle;

public class PrintService {

	public static void printMenu(String[] listMenu, String title) {
		String line = "+---------------------------------+";
		int number = 1;
		String formatTable = " %-2s. %-25s %n";

		System.out.printf("%-25s %n", title);
		System.out.println(line);

		for (String data : listMenu) {
			if (number < listMenu.length) {
				System.out.printf(formatTable, number, data);
			} else {
				System.out.printf(formatTable, 0, data);
			}
			number++;
		}
		System.out.println(line);
		System.out.println();
	}

	public static void printVechicle(List<Vehicle> listVehicle) {
		String formatTable = "| %-2s | %-15s | %-10s | %-15s | %-15s | %-5s | %-15s |%n";
		String line = "+----+-----------------+------------+-----------------+-----------------+-------+-----------------+%n";
		System.out.format(line);
		System.out.format(formatTable, "No", "Vechicle Id", "Warna", "Brand", "Transmisi", "Tahun", "Tipe Kendaraan");
		System.out.format(line);
		int number = 1;
		String vehicleType = "";
		for (Vehicle vehicle : listVehicle) {
			if (vehicle instanceof Car) {
				vehicleType = "Mobil";
			} else {
				vehicleType = "Motor";
			}
			System.out.format(formatTable, number, vehicle.getVehiclesId(), vehicle.getColor(), vehicle.getBrand(),
					vehicle.getTransmisionType(), vehicle.getYearRelease(), vehicleType);
			number++;
		}
		System.out.printf(line);
	}

	// informasi service
	public static void informationService(List<ItemService> listItemService, String vehicleId,
			List<Customer> listAllCustomer) {
		System.out.println("=====================================================================");
		System.out.println("List service yang tersedia ");
		System.out.println(
				"==========================================================================================================");
		System.out.printf("|%-5s | %-15s | %-20s | %-20s | %-20s |\n", "No", "Service ID", "Nama Service",
				"Tipe Kendaraan", "Harga");
		System.out.println(
				"==========================================================================================================");

		int[] num = { 1 };
		for (Customer customer : listAllCustomer) {
			for (Vehicle vehicle : customer.getVehicles()) {
				if (vehicle.getVehiclesId().equalsIgnoreCase(vehicleId)) {
					listItemService.stream()
							.filter(service -> service instanceof ItemService
									&& service.getVehicleType().equalsIgnoreCase(vehicle.getVehicleType()))
							.forEachOrdered(service -> {
								System.out.printf("|%-5s | %-15s | %-20s | %-20s | %-20s |\n",
										num[0],
										service.getServiceId(), service.getServiceName(), service.getVehicleType(),
										service.getPrice());
								num[0]++;
							});
					break;
				}
			}
		}

		System.out.println(
				"==========================================================================================================");
	}

}
