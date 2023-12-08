package com.bengkel.booking.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.bengkel.booking.models.BookingOrder;
import com.bengkel.booking.models.Customer;
import com.bengkel.booking.models.ItemService;
import com.bengkel.booking.repositories.CustomerRepository;
import com.bengkel.booking.repositories.ItemServiceRepository;

public class MenuService {
	private static List<Customer> listAllCustomers = CustomerRepository.getAllCustomer();
	private static List<ItemService> listAllItemService = ItemServiceRepository.getAllItemService();

	private static List<BookingOrder> bookingOrdersList = new ArrayList<>();
	private static Scanner input = new Scanner(System.in);

	public static void run() {
		boolean isLooping = true;
		do {

			boolean isLoggedIn = login(input);
			if (isLoggedIn) {
				mainMenu();
			} else {
				isLooping = false;
			}
		} while (isLooping);
	}

	public static boolean login(Scanner input) {
		String[] listMenu = { "Login", "Exit" };
		int menuChoice = 0;
		boolean isLooping = true;

		do {
			System.out.println("+---------------------------------+");
			PrintService.printMenu(listMenu, "Aplikasi Booking bengkel");
			menuChoice = Validation.validasiNumberWithRange("Masukan Pilihan Menu:", "Input Harus Berupa Angka!",
					"^[0-9]+$", listMenu.length - 1, 0);
			System.out.println(menuChoice);

			switch (menuChoice) {
				case 1:
					return BengkelService.login(listAllCustomers, input);
				default:
					System.out.println("Keluar dari aplikasi");
					isLooping = false;
					break;
			}
		} while (isLooping);

		return false;
	}

	public static void mainMenu() {
		String[] listMenu = { "Informasi Customer", "Booking Bengkel", "Top Up Bengkel Coin", "Informasi Booking",
				"Logout" };
		int menuChoice = 0;
		boolean isLooping = true;

		do {
			PrintService.printMenu(listMenu, "Booking Bengkel Menu");
			menuChoice = Validation.validasiNumberWithRange("Masukan Pilihan Menu:", "Input Harus Berupa Angka!",
					"^[0-9]+$", listMenu.length - 1, 0);
			System.out.println(menuChoice);

			switch (menuChoice) {
				case 1:
					BengkelService.informationCustomer(listAllCustomers);
					break;
				case 2:
					BengkelService.bookingMenu(listAllItemService, bookingOrdersList, input, listAllCustomers);

					break;
				case 3:
					BengkelService.topUpSaldo(listAllCustomers, input);
					break;
				case 4:

					BengkelService.informationBooking(bookingOrdersList);
					break;
				default:
					System.out.println("Logout");
					run();
					break;
			}
		} while (isLooping);

	}

}
