public class Doktor extends Kisi {
    private Bolum bolum;
    private String unvan;

    public Doktor(String ad, String soyad, String tcNo, Bolum bolum, String unvan) {
        super(ad, soyad, tcNo);
        this.bolum = bolum;
        this.unvan = unvan;
    }
    public Bolum getBolum() { 
    	return bolum; 
    }
    public String getUnvan() {
    	return unvan;
    }
    @Override
    public String toString() {
        return unvan + " " + getAd() + " " + getSoyad();
    }
}