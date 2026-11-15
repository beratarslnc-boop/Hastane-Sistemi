public class OzelSigorta implements ISigorta {
    @Override
    public double ucretHesapla(double hamUcret) {
        return hamUcret * 0.0 ; 
    }
    @Override
    public String getSigortaAdi() {
        return "Özel Sigorta";
    }
}