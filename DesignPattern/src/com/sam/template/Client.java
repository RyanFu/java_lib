package com.sam.template;

public class Client{
	public static void main(String args[]){
		Template temp = new TemplateConcrete();
		temp.update();
	}
}