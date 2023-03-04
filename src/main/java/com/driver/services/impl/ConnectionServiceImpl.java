package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{

        User user = userRepository2.findById(userId).get();
        if(user.isConnected()==true){
            throw new Exception("Already connected");
        }
        else if(countryName.equalsIgnoreCase(user.getCountry().getCountryName().toString())){
            return user;
        }
       if(user.getConnectionList().isEmpty()){
           throw new Exception("Unable to connect");
       }
       List<ServiceProvider> serviceProviderUserList = user.getServiceProviderList();
       return user;










    }
    @Override
    public User disconnect(int userId) throws Exception {

        User user = userRepository2.findById(userId).get();

        if(user.isConnected()==false){
            throw new Exception("Already disconnected");

        }
        user.setMaskedIp(null);
        user.setConnected(false);
        userRepository2.save(user);

        return user;

    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {

        User sender = userRepository2.findById(senderId).get();
        User reciever = userRepository2.findById(receiverId).get();

        String recieverCountry = reciever.getCountry().getCountryName().toString().substring(0,3).toUpperCase();
        String senderCountry = sender.getCountry().getCountryName().toString().substring(0,3).toUpperCase();

        if(!recieverCountry.equals(senderCountry)){

            int minServerProvider = Integer.MAX_VALUE;
            Country senderToBeConnected = null;

            List<ServiceProvider> serviceProviderList = reciever.getServiceProviderList();
            if(serviceProviderList.isEmpty()){
                throw new Exception("Cannot establish communication");
            }

            for(ServiceProvider serviceProvider : serviceProviderList ){
                if(serviceProvider.getId() < minServerProvider){
                    minServerProvider = serviceProvider.getId();

                }
            }

            ServiceProvider serviceProvider = serviceProviderRepository2.findById(minServerProvider).get();
            senderToBeConnected = serviceProvider.getCountryList().get(0);

            sender.setConnected(true);
            sender.setCountry(senderToBeConnected);
            return userRepository2.save(sender);


        }
        else{
            return sender;
        }


    }
}
