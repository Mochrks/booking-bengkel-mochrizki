package com.bengkel.booking.models;

import java.util.Date;
import java.util.List;
import com.bengkel.booking.interfaces.IBengkelPayment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class BookingOrder implements IBengkelPayment {
	private String bookingId;
	private String customer;
	private List<ItemService> services;
	private String paymentMethod;
	private double totalServicePrice;
	private double totalPayment;

	@Override
	public void calculatePayment() {
		double discount = 0;
		if (paymentMethod.equalsIgnoreCase("Saldo Coin")) {
			discount = getTotalServicePrice() * RATES_DISCOUNT_SALDO_COIN;
		} else {
			discount = getTotalServicePrice() * RATES_DISCOUNT_CASH;
		}

		setTotalPayment(getTotalServicePrice() - discount);
	}

}
