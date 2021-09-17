package com.in28minutes.springboot.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;

import com.in28minutes.springboot.web.model.Todo;
import com.in28minutes.springboot.web.service.TodoRepository;
import com.in28minutes.springboot.web.service.TodoService;

@Controller
public class TodoController {
	
	@Autowired
	TodoService service;
	
	@Autowired
	TodoRepository todoRepository;
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		//Data dd/MM/yyyy
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, false));
	}
	
	
    @GetMapping("/list-todos")
	public String showTodos(ModelMap model) {
    	String name=getLoggedInUserName(model);	
    	//model.put("todos",service.retrieveTodos(name));
        model.put("todos",todoRepository.findByUser(name));
		return "todos-list";
	}


	private String getLoggedInUserName(ModelMap model) {
		Object principal = SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();

		if (principal instanceof UserDetails)
			return ((UserDetails) principal).getUsername();

		return principal.toString();
	
	}
   
    @GetMapping("/add-todo")
  	public String showAddTodoPage(ModelMap model) {
    	model.addAttribute("todo",new Todo(0,getLoggedInUserName(model),"Default desc", new Date(), false));
  		return "todo";
  	}
     
    @PostMapping("/add-todo")
  	public String addTodo(ModelMap model,@Valid Todo todo,BindingResult result) {
    	
    	if(result.hasErrors()) {
    		return "todo";
    	}
    	
    	todo.setUser(getLoggedInUserName(model));
    	todoRepository.save(todo);
        //service.addTodo(getLoggedInUserName(model), todo.getDesc(), todo.getTargetDate(), false);
  		return "redirect:/list-todos";
  	}
    
	
    @GetMapping("/delete-todo")
  	public String deleteTodo(@RequestParam int id) { 	
    	//if(id==1) 
    		//throw new RuntimeException();
    	
    	todoRepository.deleteById(id);
    	//service.deleteTodo(id);
  		return "redirect:/list-todos";
  	}
    
    @GetMapping("/update-todo")
  	public String showUpdateTodoPage(@RequestParam int id,ModelMap model) { 
    	//Todo todo=service.retrieveTodo(id);
    	Todo todo=todoRepository.findById(id).get();
    	model.addAttribute("todo",todo);
  		return "todo";
  	}
    
    @PostMapping("/update-todo")
  	public String updateTodo(@Valid Todo todo,BindingResult result,ModelMap model) { 	
    	
    	if(result.hasErrors()) {
    		return "todo";
    	}
    	todo.setUser(getLoggedInUserName(model));
    	
    	//service.updateTodo(todo);
    	todoRepository.save(todo);
    	return "redirect:/list-todos";
  	}
    
   
	
	
}
