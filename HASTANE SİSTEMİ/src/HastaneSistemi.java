import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HastaneSistemi {

    // Listelerimiz
    public static List<Randevu> randevular = new ArrayList<>();
    public static List<Doktor> doktorlar = new ArrayList<>();

    // Dosya İsimleri
    private static final String DOSYA_USERS = "users.txt";
    private static final String DOSYA_RANDEVU = "randevular.txt";
    private static final String DOSYA_DOKTORLAR = "doktorlar.txt";

    // Program başlarken verileri yükle
    static {
        doktorlariDosyadanYukle();
        randevulariBellegeYukle(); 
    }

    // --- 1. DOKTORLARI DOSYADAN OKUMA ---
    private static void doktorlariDosyadanYukle() {
        File dosya = new File(DOSYA_DOKTORLAR);
        
        // Dosya yoksa hata vermesin, metottan çıksın
        if (!dosya.exists()) return;

        try (Scanner sc = new Scanner(dosya)) {
            while (sc.hasNextLine()) {
                String satir = sc.nextLine().trim();
                // Boş veya yorum satırıysa geç
                if (satir.isEmpty()) continue;
                if (satir.startsWith("//")) continue;

                // Virgülle ayır
                String[] p = satir.split(",");
                if (p.length < 5) continue; // Eksik veri varsa geç

                String ad = p[0].trim();
                String soyad = p[1].trim();
                String idSifre = p[2].trim(); // Dosyadaki 111, 222 vs.
                String bolumAdi = p[3].trim();
                String unvan = p[4].trim();

                // Bölümü bul
                Bolum bolum = bolumBul(bolumAdi);
                
                if (bolum != null) {
                    // Doktoru listeye ekle
                    doktorlar.add(new Doktor(ad, soyad, idSifre, bolum, unvan));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metin (String) olarak gelen bölümü Enum'a çevirir
    private static Bolum bolumBul(String metin) {
        for (Bolum b : Bolum.values()) {
            if (b.name().equalsIgnoreCase(metin) || b.toString().equalsIgnoreCase(metin)) {
                return b;
            }
            if (metin.equalsIgnoreCase("KBB") && b.name().contains("KULAK")) {
                return b;
            }
        }
        return null;
    }

    // --- 2. GİRİŞ İŞLEMLERİ ---
    
    // Hasta Girişi
    public static Hasta girisYap(String tc, String sifre) {
        File dosya = new File(DOSYA_USERS);
        if (!dosya.exists()) return null;

        try (Scanner sc = new Scanner(dosya)) {
            while (sc.hasNextLine()) {
                String satir = sc.nextLine().trim();
                if (satir.isEmpty()) continue;
                
                String[] p = satir.split("#");
                if (p.length < 5) continue;

                if (p[0].equals(tc) && p[1].equals(sifre)) {
                    ISigorta sigorta = sigortaBul(p[4]);
                    return new Hasta(p[2], p[3], p[0], p[1], sigorta);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Doktor Girişi
    public static Doktor doktorGirisYap(String ad, String soyad, String sifre) {
        for (Doktor d : doktorlar) {
            if (d.getAd().equalsIgnoreCase(ad) && d.getSoyad().equalsIgnoreCase(soyad)) {
                // Şifre olarak ya dosyadaki ID'yi ya da '123'ü kabul et
                if (d.getTcNo().equals(sifre) || sifre.equals("123")) {
                    return d;
                }
            }
        }
        return null;
    }

    private static ISigorta sigortaBul(String tip) {
        if (tip == null) return new SGK();
        if (tip.equalsIgnoreCase("VIP")) return new VIP();
        if (tip.equalsIgnoreCase("OZEL")) return new OzelSigorta();
        return new SGK();
    }

    // --- 3. KONTROL MEKANİZMALARI ---

    // Aynı saatte randevu var mı?
    public static boolean hastaMusaitMi(String hastaAdSoyad, LocalDate tarih, String saat) {
        for (Randevu r : randevular) {
            if (r.getHastaAdSoyad().equalsIgnoreCase(hastaAdSoyad) &&
                r.getTarih().equals(tarih) &&
                r.getSaat().equals(saat)) {
                return false; // Çakışma var
            }
        }
        return true;
    }

    // Aynı gün aynı bölümden randevu var mı?
    public static boolean ayniBolumdenRandevuVarMi(String hastaAdSoyad, Bolum bolum, LocalDate tarih) {
        for (Randevu r : randevular) {
            // Önce tarihi ve hastayı tutturmamız lazım
            if (r.getHastaAdSoyad().equalsIgnoreCase(hastaAdSoyad) && r.getTarih().equals(tarih)) {
                
                // Şimdi o randevunun doktorunu bulup bölümüne bakmalıyız
                String randevuDoktorAdi = r.getDoktorAdSoyad();
                
                for (Doktor d : doktorlar) {
                    String tamAd = d.getUnvan() + " " + d.getAd() + " " + d.getSoyad();
                    
                    if (tamAd.equalsIgnoreCase(randevuDoktorAdi)) {
                        if (d.getBolum() == bolum) {
                            return true; // Evet, bu bölümden zaten randevusu var
                        }
                    }
                }
            }
        }
        return false;
    }

    // --- 4. VERİ GETİRME ---

    // Doktorun kendi randevularını görmesi için
    public static List<Randevu> getRandevularByDoktor(String doktorAdSoyad) {
        List<Randevu> doktorunListesi = new ArrayList<>();
        for (Randevu r : randevular) {
            if (r.getDoktorAdSoyad().equalsIgnoreCase(doktorAdSoyad)) {
                doktorunListesi.add(r);
            }
        }
        return doktorunListesi;
    }

    // Bölüme göre doktorları filtrele
    public static List<Doktor> getDoktorlarByBolum(Bolum secilenBolum) {
        List<Doktor> suzulen = new ArrayList<>();
        for (Doktor d : doktorlar) {
            if (d.getBolum() == secilenBolum) {
                suzulen.add(d);
            }
        }
        return suzulen;
    }
    
    // Dolu saatleri getir (Yeşil/Kırmızı butonlar için)
    public static List<String> doluSaatleriGetir(String doktorAdSoyad, LocalDate tarih) {
        List<String> doluSaatler = new ArrayList<>();
        File dosya = new File(DOSYA_RANDEVU);
        if (!dosya.exists()) return doluSaatler;

        try (Scanner sc = new Scanner(dosya)) {
            while (sc.hasNextLine()) {
                String satir = sc.nextLine().trim();
                if (satir.isEmpty()) continue;
                String[] p = satir.split("\\s*,\\s*");
                if (p.length < 4) continue;
                
                String dDoktor = p[1].trim();
                String dTarih = p[2].trim();
                String dSaat = p[3].trim();
                
                if (dDoktor.equals(doktorAdSoyad) && dTarih.equals(tarih.toString())) {
                    doluSaatler.add(dSaat);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doluSaatler;
    }

    // --- 5. KAYIT VE SİLME İŞLEMLERİ ---

    public static void kullaniciKaydet(String ad, String soyad, String tc, String sifre, String sigortaTipi) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DOSYA_USERS, true))) {
            bw.write(tc.trim() + "#" + sifre.trim() + "#" + ad.trim() + "#" + soyad.trim() + "#" + sigortaTipi.trim());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void randevuKaydet(Randevu r) {
        randevular.add(r);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DOSYA_RANDEVU, true))) {
            bw.write(r.toString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void randevuIptalEt(String hastaAdi, String doktorAdi, LocalDate tarih, String saat) {
        File dosya = new File(DOSYA_RANDEVU);
        if (!dosya.exists()) return;

        List<String> kalanSatirlar = new ArrayList<>();
        boolean silindiMi = false;

        try (Scanner sc = new Scanner(dosya)) {
            while (sc.hasNextLine()) {
                String satir = sc.nextLine().trim();
                if (satir.isEmpty()) continue;
                String[] p = satir.split("\\s*,\\s*");
                if (p.length < 4) continue;

                String dHasta = p[0].trim();
                String dDoktor = p[1].trim();
                String dTarih = p[2].trim();
                String dSaat = p[3].trim();

                // Eşleşen satırı bulursak listeye eklemeyeceğiz (silmiş olacağız)
                if (dHasta.equals(hastaAdi) && dDoktor.equals(doktorAdi) && 
                    dTarih.equals(tarih.toString()) && dSaat.equals(saat)) {
                    silindiMi = true;
                    // Bellekten de silelim
                    randevular.removeIf(r -> r.getHastaAdSoyad().equals(hastaAdi) && 
                                             r.getTarih().equals(tarih) && 
                                             r.getSaat().equals(saat));
                    continue; 
                }
                kalanSatirlar.add(satir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Dosyayı yeniden yaz
        if (silindiMi) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(dosya, false))) {
                for (String s : kalanSatirlar) {
                    bw.write(s);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Geçmiş randevuları temizle (Sistem şişmesin)
    public static void gecmisRandevulariTemizle() {
        File dosya = new File(DOSYA_RANDEVU);
        if (!dosya.exists()) return;

        List<String> guncelSatirlar = new ArrayList<>();
        LocalDate bugun = LocalDate.now();

        try (Scanner sc = new Scanner(dosya)) {
            while (sc.hasNextLine()) {
                String satir = sc.nextLine().trim();
                if (satir.isEmpty()) continue;
                String[] p = satir.split("\\s*,\\s*");
                if (p.length < 3) continue;
                
                LocalDate randevuTarihi = LocalDate.parse(p[2].trim());
                if (!randevuTarihi.isBefore(bugun)) {
                    guncelSatirlar.add(satir);
                }
            }
        } catch (Exception e) {}
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dosya, false))) {
            for (String s : guncelSatirlar) {
                bw.write(s);
                bw.newLine();
            }
        } catch (Exception e) {}
    }
    
    // Uygulama açılınca randevuları RAM'e yükle (Performans için)
    private static void randevulariBellegeYukle() {
        File dosya = new File(DOSYA_RANDEVU);
        if (!dosya.exists()) return;
        
        randevular.clear();
        try (Scanner sc = new Scanner(dosya)) {
             while (sc.hasNextLine()) {
                String satir = sc.nextLine().trim();
                if (satir.isEmpty()) continue;
                String[] p = satir.split("\\s*,\\s*");
                if (p.length < 4) continue;
                
                Randevu r = new Randevu(p[0], p[1], LocalDate.parse(p[2]), p[3]);
                randevular.add(r);
             }
        } catch (Exception e) {}
    }
}