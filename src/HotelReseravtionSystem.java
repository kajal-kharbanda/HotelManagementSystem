    import java.sql.DriverManager;
    import java.sql.Statement;
    import java.sql.ResultSet;
    import java.sql.Connection;
    import java.sql.SQLException;
    import java.util.Scanner;


    public class HotelReseravtionSystem{
        private static final String url="jdbc:mysql://localhost:3306/hotel_db";
        private static final String username="root";
        private static final String password="kajal";

        public static void main(String[] arg)throws ClassNotFoundException, SQLException{
            try{
                Class.forName("com.mysql.cj.jdbc.Driver");
            }catch (ClassNotFoundException e){
                System.out.println(e.getMessage());
            }

            try {
                Connection con=DriverManager.getConnection(url,username,password);
                while (true){
                    System.out.println();
                    System.out.println("HOTEL MANAGEMENT SYSTEM");
                    System.out.println("1. Reserve a room");
                    System.out.println("2. View reservations");
                    System.out.println("3. Get Room Number");
                    System.out.println("4. Update Reservations");
                    System.out.println("5. Delete Reservations");
                    System.out.println("0. Exit");
                    Scanner sc=new Scanner(System.in);
                    System.out.print("Choose an option: ");
                    int choice = sc.nextInt();
                    switch (choice){
                        case 0:
                            exit(sc);
                            return;
                        case 1:
                            reserveRoom(con,sc);
                            break;
                        case 2:
                            viewReservations(con);
                            break;
                        case 3:
                            getRoomNumber(con,sc);
                            break;
                        case 4:
                            updateReservation(con,sc);
                            break;
                        case 5:
                            deleteReservation(con,sc);
                            break;

                        default:
                            System.out.println("Invalid choice. Try again.");
                    }
                }
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }

        private static void exit(Scanner sc)throws InterruptedException{
            System.out.print("Exiting System");
            int i=5;
            while(i!=0) {
                try{
                    System.out.print(".");
                    Thread.sleep(450);
                }catch (InterruptedException e){
                    System.out.println("thread interrupted. Exiting immediately.");
                }
                i--;
            }
            System.out.println();
            System.out.println("Thankyou for Using Hotel Reservation System!!!");

        }
    private    static void reserveRoom(Connection con, Scanner sc){
        try{
            System.out.print("Enter guest name: ");
            String guestName=sc.next();
            sc.nextLine();
            System.out.print("Enter room number");
            int roomNumber=sc.nextInt();
            System.out.print("Enter contact number");
            long contactNumber=sc.nextLong();

            String sql="insert into reservations (guest_name,room_number,contact_number) values('"+guestName+"',"+roomNumber+", '"+contactNumber+"')";

            try(Statement smt=con.createStatement()){
                int affectRows=smt.executeUpdate(sql);
                if(affectRows>0){
                    System.out.println("Reservation Successful!");
                }else {
                    System.out.println("Reservation failed!");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void viewReservations(Connection con)throws SQLException{
            String sql="Select reservation_id, guest_name, room_number, contact_number,reservation_date from reservations ";

            try(Statement smt=con.createStatement()){
                ResultSet resultSet=smt.executeQuery(sql);
                    System.out.println("Current Reservations:");
                    System.out.println("+---------------+--------------+---------------+---------------------+-----------------+");
                    System.out.println("|Reservation ID | Guest        | Room Number   | Contact Number      | Reservation_date" );
                    System.out.println("+---------------+--------------+---------------+---------------------+-----------------+");

                    while(resultSet.next()){
                        int reservationID = resultSet.getInt("reservation_id");
                        String guestName=resultSet.getString("guest_name");
                        int roomNumber=resultSet.getInt("room_number");
                        String contactNumber=resultSet.getString("contact_number");
                        String reservationsDate=resultSet.getTimestamp("reservation_date").toString();

                        System.out.printf("| %-13d | %-13s| %-14d| %-20s| %-19s |\n",
                                reservationID,guestName,roomNumber,contactNumber,reservationsDate);
                    }

                    System.out.println("+---------------+--------------+---------------+---------------------+-----------------+");

            }
      }
    private static  void getRoomNumber(Connection con,Scanner sc){
            try{
                System.out.println("Enter reservation ID: ");
                int reservationid=sc.nextInt();
                System.out.println("Enter guest name: ");
                String guestName=sc.next();

                String sql="Select room_number from reservations where reservation_id = " +reservationid+ " and guest_name = '" +guestName+ "'";
                try (Statement smt=con.createStatement();
                     ResultSet resultSet=smt.executeQuery(sql)){

                    if(resultSet.next()){
                        int roomNumber=resultSet.getInt("room_number");
                        System.out.println("Room number for reservation ID "+reservationid+" and guest "+guestName+" is: " +roomNumber);
                    }else{
                        System.out.println("Reservation not found forthe given ID and guest name.");
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
    }
    private static void updateReservation(Connection con, Scanner sc){
          try{
              System.out.print("Enter reservation ID to update");
              int reservationId=sc.nextInt();
              sc.nextLine();

              if(!reservationsExists(con, reservationId)){
                  System.out.println("Reservation ID not found");
                  return;
              }
              System.out.print("enter new guest name");
              String newGuestname=sc.next();
              System.out.print("enter new room number");
              int roomNumber=sc.nextInt();
              System.out.print("enter new contact number ");
              long contact=sc.nextLong();

              String sql="update reservations Set guest_name = ' "+newGuestname+"', " +
                      "room_number =" + roomNumber +", " +
                      "contact_number = " + contact + " "+
                      "where reservation_id = " + reservationId;

              try(Statement smt=con.createStatement()){
                  int affectedRows=smt.executeUpdate(sql);

                  if(affectedRows>0){
                      System.out.println("Reservation updated succesfullyt");
                  }else{
                      System.out.println("Reservation Update failed");
                  }
              }
          }catch (SQLException e){
              e.printStackTrace();
          }
    }
    private static void deleteReservation(Connection con, Scanner sc){
            try{
                System.out.print("Enter reservation ID to delete: ");
                int reservationId=sc.nextInt();

                if(!reservationsExists(con,reservationId)){
                    System.out.println("Reservation not found for thebgiven ID. ");
                    return;
                }
                String sql="Delete from reservations where reservation_id ="+reservationId;

                try(Statement smt=con.createStatement()){
                    int affectedRows=smt.executeUpdate(sql);

                    if(affectedRows>0)
                    {
                        System.out.println("Reservation deleted succesfully");
                    }else{
                        System.out.println("Reservation deletion failed");
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
    }

    private static boolean reservationsExists(Connection con,int reservationId){
            try{
                String sql="select reservation_id from reservations where reservation_id ="+reservationId;

                try(Statement smt=con.createStatement();
                ResultSet resultSet = smt.executeQuery(sql)){
                    return resultSet.next();
                }
            }catch (SQLException e){
                e.printStackTrace();
                return false;
            }
    }

    //private static void exit(Scanner sc)throws InterruptedException{
    //    System.out.print("Exiting System");
    //    int i=5;
    //    while(i!=0) {
    //        try{
    //        System.out.print(".");
    //        Thread.sleep(450);
    //        }catch (InterruptedException e){
    //            System.out.println("thread interrupted. Exiting immediately.");
    //        }
    //        i--;
    //    }
    //        System.out.println();
    //        System.out.println("Thankyou for Using Hotel Reservation System!!!");
    //
    //}
    }