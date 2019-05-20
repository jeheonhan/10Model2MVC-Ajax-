package com.model2.mvc.web.product;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;

@Controller
@RequestMapping("/product/*")
public class ProductController {
	
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;

	public ProductController() {
	}
	
	@Value("#{commonProperties['pageUnit']}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	int pageSize;
	
	@RequestMapping( value="addProduct", method=RequestMethod.POST)
	public String addProduct(@ModelAttribute("pvo") Product product,
							@RequestParam(value="file", required=false) MultipartFile imgFile) throws Exception {
		
		System.out.println("/addProduct");
		
		String manuDate = product.getManuDate();
		manuDate = manuDate.replaceAll("/", "");
		product.setManuDate(manuDate);
		
		System.out.println("imgFile.getOriginalFilename()  :: " + imgFile.getOriginalFilename());
		System.out.println("Product :: " + product);
		
		String temDir = 
				"C:\\Users\\USER\\git\\06Model2MVCShop-Presentation-BusinessLogic-\\06.Model2MVCShop(Presentation+BusinessLogic)\\WebContent\\images\\uploadFiles";
		
		if(!imgFile.isEmpty()) {
			
		}
		productService.addProduct(product);
		
		return "forward:/product/addProduct.jsp";
	}
	
	@RequestMapping( value="getProduct", method=RequestMethod.GET)
	public String getProduct(@RequestParam("prodNo") int prodNo,
							@RequestParam("menu") String menu,Model model,							
							@CookieValue(value="history", required=false) String history,
							HttpServletResponse response) throws Exception {
		
		System.out.println("==================== /getProduct 시작  ================");
		
		if(history != null) {
			if(!history.contains(""+prodNo)){	//쿠키 중복값 넣지 않게
			history += ","+prodNo;
			}
		}else {
			history = ","+prodNo;
		}		
		Cookie cookie = new Cookie("history", history);
		cookie.setPath("/");								//모든경로에서 쿠키접근 허용
		response.addCookie(cookie);		
				
		Product product = productService.getProduct(prodNo);		
		model.addAttribute("pvo", product);				
		
		if(menu.equals("manage")) {			
			return "forward:/product/updateProduct.jsp";			
		}else {
			return "forward:/product/getProduct.jsp";			
		}	
		
	}
	
	@RequestMapping( value="updateProduct", method=RequestMethod.POST)
	public String updateProduct(@ModelAttribute("product") Product product, 
								@RequestParam("prodNo") int prodNo,
								@RequestParam("manuDate") String manuDate) throws Exception{
		
		System.out.println("/updateProduct");	
		
		manuDate = manuDate.replaceAll("/", "");
		product.setManuDate(manuDate);
		
		productService.updateProduct(product);
		
		product = productService.getProduct(prodNo);
		
		System.out.println("updatePurchase 수정된 내용 확인 : "+product);
		
		
		return "redirect:/product/getProduct?prodNo="+prodNo+"&menu=confirm";
	}
	
	@RequestMapping( value="listProduct" )
	public String listProduct(@ModelAttribute("search") Search search , Model model , 
								HttpServletRequest request) throws Exception {
		
		System.out.println("/listProduct 시작=========");
		
		
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		System.out.println("search키워드 : "+search.getSearchKeyword());
		System.out.println("search컨디션 : "+search.getSearchCondition());
		Map<String , Object> map = productService.getProductList(search);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);		
		
		
		return "forward:/product/listProduct.jsp";
	}
	

}
