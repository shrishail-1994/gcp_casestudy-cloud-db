package com.example.demo;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
class DBService {
  @Autowired // This means to get the bean called userRepository
         // Which is auto-generated by Spring, we will use it to handle the data
  private UserRepository userRepository;
  
  @Autowired
  private FeedRepository feedRepository;

  public String addNewUser (User user) {
    userRepository.save(user);
    return "Saved";
  }

  public Iterable<User> getAllUsers() {
    // This returns a JSON or XML with the users
    return userRepository.findAll();
  }
  
  public Iterable<User> getUser(User user) {
	    // This returns a JSON or XML with the users
	    return userRepository.findAll();
  }
  
  public String addFeed(String data, String uName) {
	  Feed feed = new Feed();
	  
	  feed.setFeedData(data);
	  feed.setUserName(uName);
	  feed.setFdate(new Date());
	  
	  feedRepository.save(feed);
	  return "Saved";
  }
  
  public Iterable<Feed> getFeeds() {
	    // This returns a JSON or XML with the users
	    return feedRepository.findAll();
	  }
}
