public enum Bolum {
    KARDIYOLOJI("Kardiyoloji"),
    DAHILIYE("Dahiliye"),
    GOZ_HASTALIKLARI("Göz Hastalıkları"),
    CILT_HASTALIKLARI("Cilt Hastalıkları"),
    FIZIK_TEDAVI("Fizik Tedavi"),
    GOGUS_HASTALIKLARI("Göğüs Hastalıkları"),
    NOROLOJI("Nöroloji"),
    RADYOLOJI("Radyoloji"),
    ENFEKSIYON("Enfeksiyon"),
    NUKLEER_TIP("Nükleer Tıp"),
    HEMATOLOJI("Hematoloji"),
    ENDOKRIN("Endokrinoloji"),
    DIYABET("Diyabet"),
    ONKOLOJI("Onkoloji"),
    ADLI_TIP("Adli Tıp"),
    COCUK_SAGLIGI("Çocuk Sağlığı"),
    KULAK_BURUN_BOGAZ("KBB"),
    KADIN_DOGUM("Kadın Doğum"),
    SPOR_HEKIMLIGI("Spor Hekimliği"),
    UROLOJI("Üroloji");

    private String ekrandakiAd;
    
    Bolum(String ad) { 
        this.ekrandakiAd = ad; 
    }
    
    @Override
    public String toString() { 
        return ekrandakiAd; 
    }
}