package HotelBooking.service;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import HotelBooking.entities.Hotel;

public class HotelService {
    private final Map<String, Hotel> hotelsMap = new ConcurrentHashMap<>();

    public Hotel addHotel(Hotel hotel){
        return hotelsMap.put(hotel.getId(), hotel);
    }
    public Hotel getHotel(String hotelId){
        return hotelsMap.get(hotelId);
    }
    public List<Hotel> searchHotelsByCity(String city) {
        return  hotelsMap.values().stream()
            .filter(hotel -> hotel.getCity().toLowerCase(Locale.ROOT).contains(city.trim().toLowerCase()))
            .toList();
    }
}
