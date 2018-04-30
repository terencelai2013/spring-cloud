package com.javainuse.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.javainuse.services.RemoteCallService;

public class ConsumerControllerClient {

	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	private LoadBalancerClient loadBalancer;
	
	@Autowired
	private RemoteCallService remoteCallService;

	public void getEmployee() throws RestClientException, IOException {
		System.out.println("getEmployee using RemoteCallService");
		try {
			Employee emp = remoteCallService.getData();
			System.out.println(emp.getEmpId());
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
	
	public void getEmployeeLoadBalancer() throws RestClientException, IOException {
		
		ServiceInstance serviceInstance=loadBalancer.choose("employee-producer");
		
		System.out.println(serviceInstance.getUri());
		
		String baseUrl=serviceInstance.getUri().toString();
		
		baseUrl=baseUrl+"/employee";
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response=null;
		try{
		response=restTemplate.exchange(baseUrl,
				HttpMethod.GET, getHeaders(),String.class);
		}catch (Exception ex)
		{
			System.out.println(ex);
		}
		System.out.println(response.getBody());
	}
	
	public void getEmployeeEureka() throws RestClientException, IOException {

		List<ServiceInstance> instances = discoveryClient.getInstances("employee-producer");
		ServiceInstance serviceInstance = instances.get(0);

		String baseUrl = serviceInstance.getUri().toString();

		baseUrl = baseUrl + "/employee";

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(baseUrl, HttpMethod.GET, getHeaders(), String.class);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		System.out.println(response.getBody());
	}

	public void getEmployeeDirect() throws RestClientException, IOException {

		String baseUrl = "http://localhost:8080/employee";
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = null;
		try {
			response = restTemplate.exchange(baseUrl, HttpMethod.GET, getHeaders(), String.class);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		System.out.println(response.getBody());
	}

	private static HttpEntity<?> getHeaders() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<>(headers);
	}
}