package com.record.organizor.recordorganizermicroservice;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class Record {
	private String region;
	private String country;
	private String itemType;
	private String salesChannel;
	private String orderPriority;
	private String orderDate;
	private String shipDate;
	private double unitsSold;
	private double unitPrice;
	private double totalRevenue;
	private double totalCost;
	private double totalProfit;
}
