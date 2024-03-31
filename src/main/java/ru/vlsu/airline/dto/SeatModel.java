package ru.vlsu.airline.dto;

public class SeatModel {

        private int id;
        private int flightId;
        private int planeId;
        private String seatClass;
        private String number;
        private int price;
        private String status;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getFlightId() {
            return flightId;
        }

        public void setFlightId(int flightId) {
            this.flightId = flightId;
        }

        public int getPlaneId() {
            return planeId;
        }

        public void setPlaneId(int planeId) {
            this.planeId = planeId;
        }

        public String getSeatClass() {
            return seatClass;
        }

        public void setSeatClass(String seatClass) {
            this.seatClass = seatClass;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
}
