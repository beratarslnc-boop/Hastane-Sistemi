public abstract class Kisi {
    private String ad;
    private String soyad;
    private String tcNo;

    public Kisi(String ad, String soyad, String tcNo) {
        this.ad = ad;
        this.soyad = soyad;
        this.tcNo = tcNo;
    }
    public String getAd() { 
    	return ad; 
    }
    public String getSoyad() { 
    	return soyad; 
    }
    public String getTcNo() {
    	return tcNo;
    }
}