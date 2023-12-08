package com.bengkel.booking.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.bengkel.booking.models.BookingOrder;
import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.models.MemberCustomer;
import com.bengkel.booking.models.Vehicle;

public class BengkelService {
	private static int orderCounter = 1;
	private static String loggedInCustomerId = "";

	// ===================================================================================================
	// Login
	public static boolean login(List<Customer> listAllCustomers, Scanner input) {
		System.out.println("======= Login ========");
		int loginAttempts = 0;

		do {
			System.out.print("Enter username: ");
			String username = input.nextLine();
			System.out.print("Enter password: ");
			String password = input.nextLine();

			System.out.println("=======================");

			boolean customerIdFound = false;
			boolean passwordCorrect = false;

			for (Customer customer : listAllCustomers) {
				if (customer.getCustomerId().equalsIgnoreCase(username)) {
					customerIdFound = true;
					if (customer.getPassword().equals(password)) {
						passwordCorrect = true;
						System.out.println("Login successful!");
						loggedInCustomerId = customer.getCustomerId();
						return true; // Login berhasil, kembalikan true
					}
					break;
				}
			}

			if (!customerIdFound) {
				System.out.println("Customer Id Tidak Ditemukan atau Salah!");
			} else if (customerIdFound && !passwordCorrect) {
				System.out.println("Password yang Anda Masukkan Salah!");
			}

			loginAttempts++;

			if (loginAttempts > 3) {
				System.out.println("Percobaan 3 kali gagal. Anda tidak bisa login");
				System.exit(0); // Keluar dari aplikasi setelah tiga kali percobaan gagal
			}

		} while (loginAttempts < 4);

		return false;
	}

	// Info Customer
	public static void informationCustomer(List<Customer> listAllCustomer) {
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
		int num = 1;

		for (Customer customer : listAllCustomer) {

			String status = (customer instanceof MemberCustomer) ? "Member" : "Non member";

			System.out.println("===================================================================================");
			System.out
					.println("===============================  Customer Profile  ================================");
			System.out.println("===================================================================================");
			System.out.println("Customer Id : " + customer.getCustomerId());
			System.out.println("Nama : " + customer.getName());
			System.out.println("Customer Status : " + status);
			System.out.println("Alamat : " + customer.getAddress());
			if (customer instanceof MemberCustomer) {
				MemberCustomer member = (MemberCustomer) customer;
				System.out.println("Saldo Coin : " + decimalFormat.format(member.getSaldoCoin()));
			}
			System.out.println("List Kendaraan : ");
			System.out.println(
					"==================================================================================================");
			System.out.printf("|%-5s | %-13s | %-10s | %-20s | %-10s |\n", "No.",
					"Vehicle Id", "Warna", "Tipe Kendaraan", "Tahun");
			System.out.println(
					"==================================================================================================");

			for (Vehicle vehicle : customer.getVehicles()) {
				System.out.printf("|%-5s | %-13s | %-10s | %-20s | %-10s |\n", num,
						vehicle.getVehiclesId(), vehicle.getColor(), vehicle.getVehicleType(),
						vehicle.getYearRelease());
				num++;
			}

		}
		System.out.println(
				"==================================================================================================");
		System.out.print("\n");
	}

	// Booking atau Reservation
	public static void bookingMenu(List<ItemService> listItemService, List<BookingOrder> bookingOrdersList,
			Scanner input, List<Customer> listAllCustomer) {

		DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
		System.out.println("=====================================================================");
		System.out.println("Booking Bengkel ");
		System.out.println("=====================================================================");

		// input vehicle
		String vehicleId;
		boolean isVehicleFound;
		do {
			System.out.println("Silahkan Masukkan Vehicle Id: ");
			vehicleId = input.nextLine();

			isVehicleFound = false;
			for (Customer customer : listAllCustomer) {
				for (Vehicle vehicle : customer.getVehicles()) {
					if (vehicle.getVehiclesId().equalsIgnoreCase(vehicleId)) {
						isVehicleFound = true;
						break;
					}
				}
				if (isVehicleFound) {
					break;
				}
			}

			if (!isVehicleFound) {
				System.out.println("Kendaraan Tidak ditemukan.");
			}
		} while (!isVehicleFound);

		// input service
		List<ItemService> selectedServices = new ArrayList<>();
		boolean moreServices;

		PrintService.informationService(listItemService, vehicleId, listAllCustomer);
		do {
			boolean foundService = false;
			String serviceID;

			do {
				System.out.println("Silahkan Masukkan Service Id:");
				serviceID = input.nextLine();

				for (ItemService service : listItemService) {
					if (service.getServiceId().equalsIgnoreCase(serviceID)) {
						foundService = true;
						if (!selectedServices.contains(service)) {
							selectedServices.add(service);

						} else {
							System.out.println("Service sudah ada");
						}

						break;
					}
				}

				if (!foundService) {
					System.out.println("Service ID tidak ditemukan. Silahkan coba lagi.");
				}
			} while (!foundService);

			System.out.println("Apakah anda ingin menambahkan Service Lainnya? (Y/T)?");
			String choice = input.next();
			input.nextLine();
			moreServices = choice.equalsIgnoreCase("Y");
		} while (moreServices);

		System.out.println("Silahkan Pilih Metode Pembayaran (Saldo Coin atau Cash)");
		String metodePembayaran = input.nextLine();

		// ==============================================================================
		// Perhitungan
		double totalCost = 0;
		double totalPayment = 0;
		for (ItemService service : selectedServices) {
			totalCost += service.getPrice();
		}
		double discount = 0;
		if (metodePembayaran.equalsIgnoreCase("Saldo coin")) {
			discount = totalCost * 0.1;
			totalPayment = totalCost - discount;
		} else {
			totalPayment = totalCost;
		}

		// ================================================================================
		System.out.print("\n");
		System.out.println("Booking Berhasil!");
		System.out.println("Total Biaya Harga Service : " + decimalFormat.format(totalCost));
		System.out.println("Total Pembayaran : " + decimalFormat.format(totalPayment));
		System.out.print("\n");

		// simpan objek ke list

		String rsvId = generateBookingID(bookingOrdersList);
		String customerName = null;

		// Mencari nama customer yang sedang login
		for (Customer customer : listAllCustomer) {
			if (customer.getCustomerId().equalsIgnoreCase(loggedInCustomerId)) {
				customerName = customer.getName();
				break;
			}
		}

		if (customerName != null) {
			BookingOrder reservasiOrder = BookingOrder.builder()
					.bookingId(rsvId)
					.customer(customerName)
					.paymentMethod(metodePembayaran)
					.totalServicePrice(totalCost)
					.totalPayment(totalPayment)
					.services(selectedServices)
					.build();

			bookingOrdersList.add(reservasiOrder);
		} else {
			System.out.println("Nama customer tidak ditemukan.");
		}

		// update saldo coin
		for (Customer customer : listAllCustomer) {
			if (customer.getCustomerId().equals(loggedInCustomerId) && customer instanceof MemberCustomer
					&& metodePembayaran.equalsIgnoreCase("Saldo coin")) {
				MemberCustomer member = (MemberCustomer) customer;
				double currentSaldo = member.getSaldoCoin();
				double updatedSaldo = currentSaldo - totalPayment;
				member.setSaldoCoin(updatedSaldo);
				System.out.println("Pembayaran berhasil, Saldo Coin sekarang: " + decimalFormat.format(updatedSaldo));
				return;
			} else if (customer.getCustomerId().equals(loggedInCustomerId) && customer instanceof MemberCustomer
					&& metodePembayaran.equalsIgnoreCase("Cash")) {
				System.out.println("Pembayaran berhasil");
			}

		}

	}

	// Top Up Saldo Coin Untuk Member Customer
	public static void topUpSaldo(List<Customer> listAllCustomer, Scanner input) {
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
		boolean isMember = false;

		for (Customer customer : listAllCustomer) {
			if (customer.getCustomerId().equalsIgnoreCase(loggedInCustomerId) && customer instanceof MemberCustomer) {
				isMember = true;
				break;
			}
		}

		if (isMember) {
			System.out.println("Masukan besaran Top Up :");
			try {
				int topupSaldo = input.nextInt();
				input.nextLine();

				for (Customer customer : listAllCustomer) {
					if (customer.getCustomerId().equals(loggedInCustomerId) && customer instanceof MemberCustomer) {
						MemberCustomer member = (MemberCustomer) customer;
						double currentSaldo = member.getSaldoCoin();
						double updatedSaldo = currentSaldo + topupSaldo;
						member.setSaldoCoin(updatedSaldo);
						System.out.println("Top-up berhasil. Saldo sekarang: " + decimalFormat.format(updatedSaldo));
						return;
					}
				}
			} catch (InputMismatchException e) {
				System.out.println("Masukan harus berupa angka .");
			}

			System.out.println("Gagal melakukan top-up.");
		} else {
			System.out.println("Maaf fitur ini hanya untuk Member saja!.");
		}
	}

	// informasi booking order
	public static void informationBooking(List<BookingOrder> bookingOrdersList) {
		int num = 1;
		System.out.println(
				"=================================================================================================================================================================");
		System.out.printf("| %-4s | %-15s | %-15s | %-15s  | %-20s  | %-20s  | %-30s  |\n", "No", "Booking ID",
				"Nama Customer", "Payment Method", "Total Sevice", "Total Payment", "List Service");
		System.out.println(
				"=================================================================================================================================================================");

		for (BookingOrder booking : bookingOrdersList) {

			// get data-data services
			List<ItemService> services = booking.getServices();
			String serviceNames = getServiceNames(services);

			System.out.printf("| %-4s | %-15s | %-15s | %-15s  | %-20s  | %-20s  | %-30s  | \n", num,
					booking.getBookingId(), booking.getCustomer(), booking.getPaymentMethod(),
					booking.getTotalServicePrice(), booking.getTotalPayment(), serviceNames);
			num++;
		}

		System.out.println(
				"=================================================================================================================================================================");
	}

	// =====================================================================================================
	public static String getServiceNames(List<ItemService> services) {
		StringBuilder serviceNames = new StringBuilder();
		for (ItemService service : services) {
			serviceNames.append(service.getServiceName()).append(", ");
		}
		// Menghilangkan koma terakhir dan spasi
		return serviceNames.length() > 0 ? serviceNames.substring(0, serviceNames.length() - 2) : "";
	}

	public static String generateBookingID(List<BookingOrder> bookingOrdersList) {
		return "Book-" + String.format("%02d", orderCounter++);
	}

}
