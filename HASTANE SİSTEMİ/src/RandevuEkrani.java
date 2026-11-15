import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RandevuEkrani extends JFrame {

    private JComboBox<Bolum> cmbBolumler;
    private JComboBox<Doktor> cmbDoktorlar;
    private JComboBox<LocalDate> cmbTarih;
    private JLabel lblDurum, lblUcret;
    private JButton btnRandevu, btnIptal;
    
    private Hasta hasta;
    private String secilenSaat = null;
    private JButton secilenButon = null;
    private List<JButton> saatButonlari = new ArrayList<>();

    public RandevuEkrani(Hasta hasta) {
        this.hasta = hasta;
        pencereAyarlari();
        arayuzuOlustur();
        olaylariTanimla();
        
        try {
            HastaneSistemi.gecmisRandevulariTemizle();
        } catch (Exception e) {}
    }

    private void pencereAyarlari() {
        setTitle("Randevu Sistemi - " + hasta.getAd() + " " + hasta.getSoyad());
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void arayuzuOlustur() {
        JPanel pnlUst = new JPanel();
        pnlUst.setBackground(new Color(60, 90, 120));
        JLabel lblBaslik = new JLabel("Hoşgeldiniz Sayın " + hasta.getAd() + " " + hasta.getSoyad());
        lblBaslik.setForeground(Color.WHITE);
        lblBaslik.setFont(new Font("Arial", Font.BOLD, 16));
        pnlUst.add(lblBaslik);
        add(pnlUst, BorderLayout.NORTH);

        JPanel ortaPanel = new JPanel();
        ortaPanel.setLayout(new BoxLayout(ortaPanel, BoxLayout.Y_AXIS));
        ortaPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        add(ortaPanel, BorderLayout.CENTER);

        JPanel pnlSecim = new JPanel(new GridLayout(3, 2, 10, 10));
        
        pnlSecim.add(new JLabel("Tarih:"));
        cmbTarih = new JComboBox<>();
        LocalDate bugun = LocalDate.now();
        for (int i = 0; i < 15; i++) cmbTarih.addItem(bugun.plusDays(i));
        pnlSecim.add(cmbTarih);

        pnlSecim.add(new JLabel("Poliklinik:"));
        cmbBolumler = new JComboBox<>(Bolum.values());
        cmbBolumler.setSelectedIndex(-1);
        pnlSecim.add(cmbBolumler);

        pnlSecim.add(new JLabel("Doktor:"));
        cmbDoktorlar = new JComboBox<>();
        pnlSecim.add(cmbDoktorlar);
        
        ortaPanel.add(pnlSecim);

        ortaPanel.add(Box.createVerticalStrut(15));
        lblDurum = new JLabel("Lütfen Poliklinik ve Doktor Seçiniz...");
        lblDurum.setForeground(Color.RED);
        lblDurum.setAlignmentX(Component.CENTER_ALIGNMENT);
        ortaPanel.add(lblDurum);

        JPanel pnlSaatler = new JPanel(new GridLayout(4, 8, 5, 5));
        pnlSaatler.setBorder(BorderFactory.createTitledBorder("Randevu Saatleri"));
        
        int saat = 9; int dakika = 0;
        while (saat < 17) {
            String zaman = String.format("%02d:%02d", saat, dakika);
            JButton btn = new JButton(zaman);
            btn.setBackground(Color.GREEN);
            btn.setEnabled(false);
            
            String finalZaman = zaman;
            btn.addActionListener(e -> saatButonunaTiklandi(btn, finalZaman));
            
            saatButonlari.add(btn);
            pnlSaatler.add(btn);

            dakika += 15;
            if (dakika == 60) { dakika = 0; saat++; }
        }
        ortaPanel.add(pnlSaatler);

        lblUcret = new JLabel("Tutar: 0.0 TL");
        lblUcret.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUcret.setFont(new Font("Arial", Font.BOLD, 15));
        ortaPanel.add(Box.createVerticalStrut(10));
        ortaPanel.add(lblUcret);

        JPanel altPanel = new JPanel();
        btnRandevu = new JButton("RANDEVU AL");
        btnRandevu.setBackground(new Color(40, 167, 69));
        btnRandevu.setForeground(Color.WHITE);
        btnRandevu.setEnabled(false);

        btnIptal = new JButton("İPTAL ET");
        btnIptal.setBackground(Color.RED);
        btnIptal.setForeground(Color.WHITE);
        btnIptal.setEnabled(false);

        altPanel.add(btnRandevu);
        altPanel.add(btnIptal);
        add(altPanel, BorderLayout.SOUTH);
    }

    private void olaylariTanimla() {
        cmbBolumler.addActionListener(e -> {
            cmbDoktorlar.removeAllItems();
            Bolum secilen = (Bolum) cmbBolumler.getSelectedItem();
            if (secilen != null) {
                for (Doktor d : HastaneSistemi.getDoktorlarByBolum(secilen)) {
                    cmbDoktorlar.addItem(d);
                }
            }
            ekraniTazele();
        });

        cmbDoktorlar.addActionListener(e -> ekraniTazele());
        cmbTarih.addActionListener(e -> ekraniTazele());

        btnRandevu.addActionListener(e -> randevuOlustur());
        btnIptal.addActionListener(e -> randevuIptal());
    }

    private void saatButonunaTiklandi(JButton btn, String zaman) {
        if (btn.getBackground().equals(Color.RED)) {
            lblDurum.setText("Seçili: " + zaman + " (DOLU - İptal edebilirsiniz)");
            secilenButon = btn;
            secilenSaat = zaman;
            btnRandevu.setEnabled(false);
            btnIptal.setEnabled(true);
        } else {
            if (secilenButon != null && !secilenButon.getBackground().equals(Color.RED)) {
                secilenButon.setBackground(Color.GREEN);
            }
            btn.setBackground(Color.ORANGE);
            secilenButon = btn;
            secilenSaat = zaman;
            
            lblDurum.setText("Seçili: " + zaman + " (MÜSAİT - Alabilirsiniz)");
            double ucret = hasta.getSigorta().ucretHesapla(5000);
            lblUcret.setText("Ödenecek: " + ucret + " TL");
            
            btnRandevu.setEnabled(true);
            btnIptal.setEnabled(false);
        }
    }

    private void ekraniTazele() {
        Doktor dr = (Doktor) cmbDoktorlar.getSelectedItem();
        LocalDate trh = (LocalDate) cmbTarih.getSelectedItem();
        
        secilenSaat = null;
        secilenButon = null;
        btnRandevu.setEnabled(false);
        btnIptal.setEnabled(false);
        lblUcret.setText("Tutar: 0.0 TL");

        if (dr == null) return;

        List<String> doluSaatler = HastaneSistemi.doluSaatleriGetir(
             dr.getUnvan() + " " + dr.getAd() + " " + dr.getSoyad(), trh
        );

        for (JButton btn : saatButonlari) {
            btn.setEnabled(true);
            if (doluSaatler.contains(btn.getText())) {
                btn.setBackground(Color.RED);
            } else {
                btn.setBackground(Color.GREEN);
            }
        }
        lblDurum.setText("Lütfen bir saat seçiniz.");
    }

    private void randevuOlustur() {
        Doktor dr = (Doktor) cmbDoktorlar.getSelectedItem();
        LocalDate trh = (LocalDate) cmbTarih.getSelectedItem();
        
        if (dr != null && secilenSaat != null) {
            String hastaAdSoyad = hasta.getAd() + " " + hasta.getSoyad();

            // 1. KONTROL: Aynı saatte başka randevusu var mı?
            if (!HastaneSistemi.hastaMusaitMi(hastaAdSoyad, trh, secilenSaat)) {
                JOptionPane.showMessageDialog(this, 
                    "HATA: Aynı tarih ve saatte başka bir randevunuz var!", 
                    "Çakışma", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. KONTROL: Aynı bölümden bugün randevusu var mı? (YENİ EKLEDİK)
            Bolum secilenBolum = (Bolum) cmbBolumler.getSelectedItem();
            if (HastaneSistemi.ayniBolumdenRandevuVarMi(hastaAdSoyad, secilenBolum, trh)) {
                JOptionPane.showMessageDialog(this, 
                    "DİKKAT: " + secilenBolum + " bölümünden bugün zaten bir randevunuz var.\n" +
                    "Aynı gün aynı bölümden ikinci randevu alınamaz.", 
                    "Bölüm Kısıtlaması", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Randevu r = new Randevu(hastaAdSoyad, 
                                    dr.getUnvan()+" "+dr.getAd()+" "+dr.getSoyad(), trh, secilenSaat);
            
            HastaneSistemi.randevuKaydet(r);
            JOptionPane.showMessageDialog(this, "Randevu Alındı! \nSaat: " + secilenSaat);
            ekraniTazele();
        }
    }

    private void randevuIptal() {
        Doktor dr = (Doktor) cmbDoktorlar.getSelectedItem();
        LocalDate trh = (LocalDate) cmbTarih.getSelectedItem();
        
        if (dr != null && secilenSaat != null) {
            int onay = JOptionPane.showConfirmDialog(this, "İptal edilsin mi?");
            if (onay == JOptionPane.YES_OPTION) {
                HastaneSistemi.randevuIptalEt(hasta.getAd()+" "+hasta.getSoyad(), 
                                              dr.getUnvan()+" "+dr.getAd()+" "+dr.getSoyad(), trh, secilenSaat);
                JOptionPane.showMessageDialog(this, "İptal Edildi.");
                ekraniTazele();
            }
        }
    }
}