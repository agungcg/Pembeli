package ptk111.com.pembeli.Database;

/**
 * Created by ptk111 on 5/16/2017.
 */

public class LokasiUser extends Koneksi {
    String URL = "http://agungcahya.esy.es/server.php";
    String url = "";
    String response = "";

    public String tampilLokasiPedagang() {
        try {
            url = URL + "?operasi=viewPedagang";
            System.out.println("URL Tampil Lokasi Pedagang: " + url);
            response = call(url);
        } catch (Exception e) {
        }
        return response;
    }

    public String updateLokasiPembeliById(Integer id, Double latitude, Double longitude) {
        try {
            url = URL + "?operasi=updatePembeliById&id=" + id + "&latitude=" + latitude + "&longitude=" + longitude;
            System.out.println("URL Update Lokasi Pembeli By ID : " + url);
            response = call(url);
        } catch (Exception e) {
        }
        return response;
    }

    public String createAkun(String nama, String telepon ,String username, String password) {
        try {
            url = URL + "?operasi=insertPembeli&nama=" + nama + "&telepon=" + telepon + "&username=" + username + "&password=" + password;
            System.out.println("URL Insert Biodata : " + url);
            response = call(url);
        } catch (Exception e) {
        }
        return response;
    }
}
