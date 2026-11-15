public class VIP implements ISigorta {
    @Override
    public double ucretHesapla(double hamUcret) {
        return hamUcret * 2.0 ;
    }
    @Override
    public String getSigortaAdi() {
        return "VIP";
    }
}