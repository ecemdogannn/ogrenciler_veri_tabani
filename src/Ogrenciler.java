import java.sql.*;
import java.util.Scanner;

public class Ogrenciler {
    private String kullanici_adi = "root";
    private String parola = "";
    private String db_ismi = "ogrenciler";
    private String host = "localhost";
    private int port = 3306;
    private Connection con = null;
    private Statement stm = null;
    private PreparedStatement pre = null ;

    public void Table(){
        try {
            stm = con.createStatement();
            String SQLsorgu = " CREATE TABLE notlar (id INT PRIMARY KEY AUTO_INCREMENT, ogr_ad TEXT, ogr_soyad TEXT, ogr_not INT)";
            stm.execute(SQLsorgu);
            System.out.println("tablo oluşturuldu.");

        } catch (SQLException e) {
            System.out.println("tablo oluşturulamadı." + e.getMessage());
            e.printStackTrace();
        }
    }
    //1.YÖNTEM
    public void ogrenciEkle(){
        try {
            stm = con.createStatement();

            String ad = "Ecem";
            String soyad = "Doğan";
            int not = 90;
            String SQLsorgu = " INSERT INTO notlar (ogr_ad,ogr_soyad,ogr_not) VALUES (" + "'" + ad + "'," + "'" + soyad + "'," + "'" + not + "')";
            stm.execute(SQLsorgu);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //2.Yöntem
    public void preparedOgrenciEkle(){
        String sorgu = "INSERT INTO notlar (ogr_ad,ogr_soyad,ogr_not) VALUES (?,?,?)";
        try {
            pre = con.prepareStatement(sorgu);
//            pre.setString(1,"Ceren"); *Bunu yorum satırına alma sebebim her çalıştırdığımda ceren adlı öğrenciyi eklemeye çalışacak.
//            pre.setString(2, "Arslantas"); *Ayrıca silip yeni bir isimde yazabilirsin ayrı oluşturmak yerine. Aşağıdakini öyle yapacağım.
//            pre.setInt(3, 100); *Önce yıldız adlı öğrenciyi ekledim sonra onu silip farklı bir öğrenci ekleyeceğim.
            pre.setString(1, "Ceyhun");
            pre.setString(2, "Aslan"); ;
            pre.setInt(3, 60);
            pre.executeUpdate();
            System.out.println("tabloya öğrenci eklendi");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //3.Yöntem
    //Kullanıcıdan veri alarak öğrenci ekleme
    public void KullanıcıOgrenciEkleme(){
        Scanner input = new Scanner(System.in);
        System.out.println("lütfen ekleyeceğiniz öğrencinin adını giriniz.");
        String ogr_ad = input.nextLine();
        System.out.println("lütfen ekleyeceğiniz öğrencinin soyadını giriniz.");
        String ogr_soyad = input.nextLine();
        System.out.println("lütfen ekleyeceğiniz öğrencinin aldığı notu giriniz.");
        int ogr_not = input.nextInt();

        String sorgu = "INSERT INTO notlar (ogr_ad,ogr_soyad,ogr_not) VALUES (?,?,?)";
        try {
            pre = con.prepareStatement(sorgu);
            pre.setString(1,ogr_ad);
            pre.setString(2,ogr_soyad);
            pre.setInt(3,ogr_not);
            pre.executeUpdate();
            System.out.println("Kullanıcıdan alınan bilgilerle tabloya öğrenci otomotik olarak eklendi.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void OgrencileriGöster(){
             //Burada index oluşturup o indexi çalıştırıyoruz.
            // İndeks oluşturma sorgusu
            String sorguIndeks = "CREATE INDEX İdx_ogr_adı4 ON notlar (ogr_ad)";

            // Veri çekme sorgusu
            String sorguVeri = "SELECT * FROM notlar WHERE ogr_ad = ?";

            try {
                pre = con.prepareStatement(sorguIndeks);
                pre.executeUpdate();

                System.out.println("******************************");
                pre = con.prepareStatement(sorguVeri);
                pre.setString(1, "Ecem"); //
                ResultSet rs = pre.executeQuery();

                 //Sonuçları Göster
                while (rs.next()) {
                    String ogr_ad = rs.getString("ogr_ad");
                    String ogr_soyad = rs.getString("ogr_soyad");
                    int ogr_not = rs.getInt("ogr_not");

                    System.out.println("Ad: " + ogr_ad + ", Soyad: " + ogr_soyad + ", Not: " + ogr_not);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

    }
    public void Sırala(){
        //String Sorgu = "SELECT * FROM notlar ORDER BY ogr_not ASC"; *Notları küçükten büyüğe sıralar.
        String Sorgu = "SELECT * FROM notlar ORDER BY ogr_not DESC"; //Notları Büyükten Küçüğe sıralar.

        try {
            pre = con.prepareStatement(Sorgu);
            ResultSet rs = pre.executeQuery();
            while (rs.next()){

                String ogr_ad = rs.getString("ogr_ad");
                int ogr_not = rs.getInt("ogr_not");
                System.out.println("Öğrenci Adı: " + ogr_ad + " Öğrencinin Notu: " + ogr_not);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    public void GroupBy(){
        // Bu SQL sorgusu, notlar tablosundaki verileri ogr_bolum sütununa göre gruplayarak her bir grup için ogr_bolum değerini
        // ve bu grupların kaç adet satırdan oluştuğunu COUNT(*) döndürür.
        String sorgu = "SELECT ogr_bolum,COUNT(*) AS 'Ögrenci Bölüm Sayısı' FROM notlar GROUP BY ogr_bolum";
        try {
            pre = con.prepareStatement(sorgu);
            ResultSet rs = pre.executeQuery();
            while (rs.next()){

                String ogr_bolum = rs.getString("ogr_bolum");
                int ogrenciSayisi = rs.getInt("Ögrenci Bölüm Sayısı");
                System.out.println("Bölüm: " + ogr_bolum +  " /"+ " Bölüm Sayısı: " + ogrenciSayisi);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public void SütunEkle(){
        //String sorgu = "ALTER TABLE notlar add column ogr_bolum TEXT"; *Tabloya yeni bir sütun eklendi.
        String sorgu2 = "UPDATE notlar SET ogr_bolum = ? WHERE id = 1"; //Yeni sütuna id si 1 olan öğrencinin bölümü eklendi.
        try {
//            pre = con.prepareStatement(sorgu);
//            pre.executeUpdate();

            pre = con.prepareStatement(sorgu2);
            pre.setString(1,"Bilgisayar");
            pre.executeUpdate();
            System.out.println("sütun eklendi ve sütuna bilgisayar bölümü kayıt edildi.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void yeni(){
        try {
//            stm = con.createStatement();
//            String SQLsorgu = " CREATE TABLE notlar2 (id INT PRIMARY KEY AUTO_INCREMENT, ogr_ad TEXT, ogr_soyad TEXT, ogr_not INT, ogr_bolum TEXT)";
//            stm.execute(SQLsorgu);
//            System.out.println("tablo oluşturuldu.");

            String Sorgu = "INSERT INTO notlar2 ( ogr_ad , ogr_soyad , ogr_not , ogr_bolum ) VALUES(?,?,?,?)";
            pre= con.prepareStatement(Sorgu);
            pre.setString(1,"İdil");
            pre.setString(2,"Boz");
            pre.setInt(3, 100);
            pre.setString(4, "Bilgisayar");
            pre.executeUpdate();
            System.out.println(" tabloya öğrenci eklendi.");
        } catch (SQLException e) {
            System.out.println("tablo oluşturulamadı." + e.getMessage());
            e.printStackTrace();
        }
    }
    public void Join(){
        String sorgu = "SELECT * FROM notlar INNER JOIN notlar2 on notlar.ogr_not = notlar2.ogr_not"; //notlar ve notlar2 tablosunu ogr_not üzerinden birleştirir.
        // ve notlar tablosundaki eşleşen değerleri getirir.
        try {
            pre= con.prepareStatement(sorgu);
            ResultSet rs = pre.executeQuery();
            while (rs.next()){
                String ogr_ad = rs.getString("ogr_ad");
                String ogr_soyad = rs.getString("ogr_soyad");
                int ogr_not = rs.getInt("ogr_not");
                System.out.println("Ad: " + ogr_ad + ", Soyad: " + ogr_soyad + ", Not: " + ogr_not);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    public void Join2(){
        //notlar ve notlar2 tablosunu ogr_not üzerinden birleştirir.
        // ve notlar tablosundaki eşleşen değerlerin ortalamasını getirir.
        String sorgu = "SELECT AVG(notlar.ogr_not) AS ortalama_not FROM notlar INNER JOIN notlar2 ON notlar.ogr_not = notlar2.ogr_not ";
        try {
            pre= con.prepareStatement(sorgu);
            ResultSet rs = pre.executeQuery();
            while (rs.next()){

                double ortalama_not = rs.getDouble("ortalama_not");
                System.out.println("Ortalama Not: " + ortalama_not);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Ogrenciler(){
        String url = "jdbc:mysql://" + host + ":" + port + "/" + db_ismi+ "?useUnicode=true&characterEncoding=utf8";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver bulunamadı...");
        }
        try {
            con = DriverManager.getConnection(url,kullanici_adi,parola);
            System.out.println("Bağlantılı başarılı...");
        } catch (SQLException e) {
            System.out.println("Bağlantı başarısız...");
        }
    }


    public static void main(String[] args) {
        Ogrenciler ogrn = new Ogrenciler ();
        //ogrn.Table();
        // ogrn.ogrenciEkle();
        // ogrn.preparedOgrenciEkle();
        //ogrn.KullanıcıOgrenciEkleme();
        ogrn.OgrencileriGöster();
        // ogrn.Sırala();
        //ogrn.SütunEkle();
        //ogrn.GroupBy();
        // ogrn.yeni();
        ogrn.Join();
        ogrn.Join2();
    }
}
