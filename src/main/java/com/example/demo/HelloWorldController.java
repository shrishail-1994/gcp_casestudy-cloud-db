package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RestController;  

//image upload
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


@RestController  
public class HelloWorldController   
{  
	
	Map<String, User> userDetails = new HashMap();
	
	@Autowired
	DBService dbService;

	@Autowired    
	ImageRepository imageRepository;

	
	@RequestMapping("/test")  
	public String hello() {  
		return "Hello javaTpoint";  
	}  
	
	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/authenticate")  
	public Response authenticate(@RequestBody Credentials credentials) {		
		
		Iterable<User> userList = dbService.getAllUsers();
		Response response = new Response();

		for (User user2 : userList) {
			if(user2.getUserName().equalsIgnoreCase(credentials.getUserName()) && user2.getPassword().equalsIgnoreCase(credentials.getPassword())){
				response.setStatus("success");
				return response;
			}
		}		
		response.setStatus("failed"); 
		return response;
	}  
	
	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/register")  
	public Response register(@RequestBody User userData) {		
		userDetails.put(userData.getUserName(), userData);		
		
		dbService.addNewUser(userData);
		
		Response response = new Response();
		response.setStatus("success");
		return response;
	}  
	
	
	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping("/getFeed")  
	public List<Feed> getFeed() {		
		Iterable<Feed> feedIterator = dbService.getFeeds();		
		List<Feed> feedList = new ArrayList<Feed>();
		feedIterator.forEach(feedList::add);
		
		feedList.sort(Comparator.comparing(Feed::getFdate));
		Collections.reverse(feedList);

		
		return feedList;
	}  
	
	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/addFeed")  
	public Response addFeed(@RequestBody Feed data) {		
		
		dbService.addFeed(data.getFeedData(), data.getUserName());		
		Response response = new Response();
		response.setStatus("success");
		return response;
	}

	//image upload
	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/uploadImage")
    public Response uplaodImage(@RequestParam MultipartFile file)
            throws IOException {

		System.out.println("Original Image Byte Size - " + file.getBytes().length);
		Picture img = new Picture(file.getOriginalFilename(), file.getContentType(),
				compressBytes(file.getBytes()));
		//imageRepository.save(img);
        Response response = new Response();
		response.setStatus("success");
		return response;
    }

	// compress the image bytes before storing it in the database
		public static byte[] compressBytes(byte[] data) {
			Deflater deflater = new Deflater();
			deflater.setInput(data);
			deflater.finish();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
			byte[] buffer = new byte[1024];
			while (!deflater.finished()) {
				int count = deflater.deflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			try {
				outputStream.close();
			} catch (IOException e) {
			}
			System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);

			return outputStream.toByteArray();
		}
		
		
		// uncompress the image bytes before returning it to the angular application
		public static byte[] decompressBytes(byte[] data) {
			Inflater inflater = new Inflater();
			inflater.setInput(data);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
			byte[] buffer = new byte[1024];
			try {
				while (!inflater.finished()) {
					int count = inflater.inflate(buffer);
					outputStream.write(buffer, 0, count);
				}
				outputStream.close();
			} catch (IOException ioe) {
			} catch (DataFormatException e) {
			}
			return outputStream.toByteArray();
		}
		
		/*
		 * @CrossOrigin(origins = "http://localhost:4200")
		 * 
		 * @GetMapping("/getImage") public Picture getImage() throws IOException {
		 * 
		 * final Optional<Picture> retrievedImage =
		 * imageRepository.findByName("laptop1.jpg"); Picture img = new
		 * Picture(retrievedImage.get().getName(), retrievedImage.get().getType(),
		 * decompressBytes(retrievedImage.get().getPic())); return img; }
		 */
		
		@CrossOrigin(origins = "http://localhost:4200")
		@GetMapping("/getAllImage")
		public List<Picture> getAllImage() throws IOException {

			final Iterable<Picture> retrievedImage = null;//imageRepository.findAll();
			
			List<Picture> responseList = new ArrayList<>();			
			for (Picture picture : retrievedImage) {
				Picture img = new Picture(picture.getName(), picture.getType(),
						decompressBytes(picture.getPic()));
				responseList.add(img);
			}			
			return responseList;
		}
}
