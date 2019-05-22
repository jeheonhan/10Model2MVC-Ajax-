package com.model2.mvc.web.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;

@RestController
@RequestMapping("/product/*")
public class ProductRestController {
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;

	public ProductRestController() {
		System.out.println(this.getClass());
	}
	
	@RequestMapping(value="json/getProduct/{prodNo}", method=RequestMethod.GET)
	public Product getProduct(@PathVariable int prodNo) throws Exception{
		
		System.out.println("json getProduct 시작@@@@@@@@@@@@@");
		
		return productService.getProduct(prodNo);
	}
	
	@RequestMapping(value="json/addProduct", method=RequestMethod.POST)
	public void addProduct(@RequestBody Product product) throws Exception{
		
		System.out.println("json/addProduct 시작@@@@@@@@@@@@@");
		
		String manuDate = product.getManuDate();
		manuDate = manuDate.replaceAll("-", "");
		product.setManuDate(manuDate);		
		
		System.out.println("바인딩 된 Product객체 : "+product);
		
		productService.addProduct(product);		
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="json/listProduct", method=RequestMethod.POST)
	public List<String> listProduct(@RequestBody Search search) throws Exception{
		
		System.out.println("json/listProduct 시작@@@@@@@@@@@@@");
		
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		
		Map<String, Object> map = productService.getProductList(search);
		int pageSize = (Integer)map.get("totalCount");
		
		System.out.println("pageSize : "+pageSize);
		
		search.setPageSize(pageSize);
				
		map = productService.getProductList(search);
		
		System.out.println("map 확인 : "+map);
		System.out.println();
		System.out.println("map list 확인 : "+map.get("list"));
		System.out.println();
		
		List<Product> productList = (List<Product>) map.get("list");
		
		System.out.println("list Size : "+productList.size());
		
		List<String> prodNameList = new ArrayList<String>();
		
		for(int i=0; i<productList.size(); i++) {			
			prodNameList.add(productList.get(i).getProdName());
		}						
		
		System.out.println("prodNameList : "+prodNameList);
		
		return prodNameList;
				
		
	}
	
	

}
