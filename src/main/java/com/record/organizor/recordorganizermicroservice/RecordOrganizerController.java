package com.record.organizor.recordorganizermicroservice;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
public class RecordOrganizerController {

	private static final AmazonDynamoDB DDB_CLIENT = AmazonDynamoDBClientBuilder.standard().build();
	private static final DynamoDB DDB = new DynamoDB(DDB_CLIENT);
	public static final String SALES_RECORDS = "SalesRecords";
	public static final String KEY = "key";

	@PostMapping("/save")
	public List<String> saveRecords(@RequestBody List<Record> records) {
		final List<String> uuids = new LinkedList<>();
		try {
			final TableWriteItems writeItems = new TableWriteItems(SALES_RECORDS);
			for (Record record : records) {
				final String key = UUID.randomUUID().toString();
				uuids.add(key);
				writeItems.addItemToPut(new Item().withPrimaryKey(KEY, key).withString("region", record.getRegion())
						.withString("country", record.getCountry()).withString("itemType", record.getItemType())
						.withString("salesChannel", record.getSalesChannel()).withString("orderPriority", record.getOrderPriority())
						.withString("orderDate", record.getOrderDate())
						.withString("shipDate", record.getShipDate()).withDouble("unitsSold", record.getUnitsSold())
						.withDouble("unitPrice", record.getUnitPrice()).withDouble("totalRevenue", record.getTotalRevenue())
						.withDouble("totalCost", record.getTotalCost()).withDouble("totalProfit", record.getTotalProfit()));
			}
			BatchWriteItemOutcome outcome = DDB.batchWriteItem(writeItems);
			do {
				Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();

				if (outcome.getUnprocessedItems().size() == 0)
					System.out.println("No unprocessed items found");
				else {
					System.out.println("Retrieving the unprocessed items");
					outcome = DDB.batchWriteItemUnprocessed(unprocessedItems);
				}
			} while (outcome.getUnprocessedItems().size() > 0);
		} catch (Exception e) {
			log.error("Error while saving records to DynamoDB {}", e.getMessage(), e);
		}

		return uuids;
	}

	@GetMapping("/recordId/{recordId}")
	public Record getRecordById(@PathVariable("recordId") String recordId) {
		try {
			final Item item = DDB.getTable(SALES_RECORDS).getItem(KEY, recordId);
			if (item != null) {
				final Record record = new Record();
				record.setRegion(item.getString("region"));
				record.setCountry(item.getString("country"));
				record.setItemType(item.getString("itemType"));
				record.setSalesChannel(item.getString("salesChannel"));
				record.setOrderPriority(item.getString("orderPriority"));
				record.setOrderDate(item.getString("orderDate"));
				record.setShipDate(item.getString("shipDate"));
				record.setUnitsSold(item.getDouble("unitsSold"));
				record.setUnitPrice(item.getDouble("unitPrice"));
				record.setTotalRevenue(item.getDouble("totalRevenue"));
				record.setTotalCost(item.getDouble("totalCost"));
				record.setTotalProfit(item.getDouble("totalProfit"));
				return record;
			}
		} catch (Exception e) {
			log.error("Error while getting record from DynamoDB {}", e.getMessage(), e);
		}

		return null;
	}
}
