import java.time.LocalDate;

public class Randevu {

    private String hastaAdSoyad;
    private String doktorAdSoyad;
    private LocalDate tarih;
    private String saat;

    public Randevu(String hastaAdSoyad, String doktorAdSoyad, LocalDate tarih, String saat) {
        this.hastaAdSoyad = hastaAdSoyad;
        this.doktorAdSoyad = doktorAdSoyad;
        this.tarih = tarih;
        this.saat = saat;
    }
    public String getHastaAdSoyad() {
        return hastaAdSoyad;
    }
    public String getDoktorAdSoyad() {
        return doktorAdSoyad;
    }
    public LocalDate getTarih() {
        return tarih;
    }
    public String getSaat() {
        return saat;
    }
    @Override
    public String toString() {
        return hastaAdSoyad + " , " + doktorAdSoyad + " , " + tarih + " , " + saat;
    }
}
