public class SGK implements ISigorta {
    @Override
    public double ucretHesapla(double hamUcret) {
        return hamUcret * 0.60; 
    }
    @Override
    public String getSigortaAdi() {
        return "SGK";
    }
}