public class Hasta extends Kisi {
    private String sifre;
    private ISigorta sigorta;

    public Hasta(String ad, String soyad, String tcNo, String sifre, ISigorta sigorta) {
        super(ad, soyad, tcNo);
        this.sifre = sifre;
        this.sigorta = sigorta;
    }
    public String getSifre() { 
    	return sifre;
    }
    public ISigorta getSigorta() {
    	return sigorta;
    }
}