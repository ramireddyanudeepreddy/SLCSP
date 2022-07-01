package com.anudeep.slcsp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class CalculateSlcsp {
	
	
	public static Map<String, SortedSet<Double>> readPlans(String planFile) throws IOException {		
		BufferedReader reader = null;
		String plan = "";
		Map<String, SortedSet<Double>> plansMap = new HashMap<>();
		try {
			reader = new BufferedReader(new FileReader(planFile, StandardCharsets.UTF_8));
			
			//Ignoring Header
			plan = reader.readLine();
			
			while((plan = reader.readLine()) != null) {
				String[] curPlan = plan.split(",");
				String state = curPlan[1];
				String metal = curPlan[2];
				Double rate = Double.valueOf(curPlan[3]);
				if(metal.equalsIgnoreCase("silver")) {
					plansMap.putIfAbsent(state, new TreeSet<>());
					SortedSet<Double> planRate = plansMap.get(state);
					planRate.add(rate);
					if(planRate.size()>2) planRate.remove(planRate.last());		
					plansMap.put(state, planRate);
				}				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			reader.close();
		}
		return plansMap;
	}
	
	
	private static Map<String, String> readZips(String zipFile) throws IOException {
		BufferedReader reader = null;
		String zipData = "";
		Map<String, String> zips = new HashMap<>();
		try {
			reader = new BufferedReader(new FileReader(zipFile, StandardCharsets.UTF_8));
			
			//Ignoring Header
			zipData = reader.readLine();
			
			while((zipData = reader.readLine()) != null) {
				String[] zip = zipData.split(",");
				String zipCode = zip[0];
				String State = zip[1];
				zips.putIfAbsent(zipCode, State);				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			reader.close();
		}
		return zips;
	}
	
	
	public static void main(String[] args) throws IOException {
		System.out.println(args.length);
		String codePath = System.getProperty("user.dir");
		System.out.println(codePath);
		String planFile =  args.length > 0 ? args[0] : codePath+"/src/data/plans.csv";
		String zipFile = args.length > 1 ? args[1] : codePath+"/src/data/zips.csv";
		String slcspFile = args.length > 2 ? args[2] : codePath+"/src/data/slcsp.csv";
		Map<String, SortedSet<Double>> plans = readPlans(planFile);
		Map<String, String> zips = readZips(zipFile);
		updateSLCSP(plans, zips, slcspFile);
	}


	private static void updateSLCSP(Map<String, SortedSet<Double>> plans, Map<String, String> zips, String slcspFile) throws IOException {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		StringBuffer slcspDataUpdated = new StringBuffer();
		try {
			reader = new BufferedReader(new FileReader(slcspFile, StandardCharsets.UTF_8));
			
			//Ignoring Header
			String header = reader.readLine();
			slcspDataUpdated.append(header);
			
			String slcspData;
			while((slcspData = reader.readLine()) != null) {
				String[] slcsp = slcspData.split(",");
				String zipCode = slcsp[0];
				String state = zips.getOrDefault(zipCode, "");
				SortedSet<Double> bestRates = plans.getOrDefault(state,new TreeSet<>());
				String secodBestRate = bestRates.size() == 2 ? String.format("%.2f", bestRates.last()) : "";	
				slcspDataUpdated.append("\n"+zipCode+","+secodBestRate);				
				//System.out.println("`"+zipCode+","+secodBestRate+"`");
			}
			try{
				writer = new BufferedWriter(new FileWriter(slcspFile, StandardCharsets.UTF_8));
				writer.write(slcspDataUpdated.toString());
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				writer.close();
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			reader.close();
		}
		System.out.println(slcspDataUpdated);
	}	
}

