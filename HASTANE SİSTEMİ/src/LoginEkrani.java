import javax.swing.*;
import java.awt.*;

public class LoginEkrani extends JFrame {

    private JTextField txtTC;
    private JPasswordField txtSifre;
    private JButton girisButonu;
    private JButton kayitButonu;
    private JButton doktorGirisButonu; 

    public static void main(String[] args) {
        try { HastaneSistemi.gecmisRandevulariTemizle(); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new LoginEkrani().setVisible(true));
    }

    public LoginEkrani() {
        setTitle("MEDPLUS HASTANESİ - GİRİŞ");
        setSize(450, 420); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null); 

        getContentPane().setBackground(new Color(44, 62, 80)); 

        JLabel baslikLabel = new JLabel("HASTANE GİRİŞİ");
        baslikLabel.setForeground(Color.WHITE);
        baslikLabel.setFont(new Font("Arial", Font.BOLD, 24));
        baslikLabel.setBounds(125, 30, 250, 30);
        add(baslikLabel);

        JLabel lblTC = new JLabel("TC Kimlik No:");
        lblTC.setForeground(Color.WHITE);
        lblTC.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTC.setBounds(50, 100, 100, 25);
        add(lblTC);

        txtTC = new JTextField();
        txtTC.setBounds(150, 100, 200, 30);
        add(txtTC);

        JLabel lblSifre = new JLabel("Şifre:");
        lblSifre.setForeground(Color.WHITE);
        lblSifre.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSifre.setBounds(50, 150, 100, 25);
        add(lblSifre);

        txtSifre = new JPasswordField();
        txtSifre.setBounds(150, 150, 200, 30);
        add(txtSifre);

        girisButonu = new JButton("HASTA GİRİŞİ");
        girisButonu.setBounds(150, 210, 120, 35);
        girisButonu.setBackground(new Color(46, 204, 113));
        girisButonu.setForeground(Color.WHITE);
        add(girisButonu);

        kayitButonu = new JButton("KAYIT OL");
        kayitButonu.setBounds(280, 210, 95, 35);
        kayitButonu.setBackground(new Color(52, 152, 219));
        kayitButonu.setForeground(Color.WHITE);
        add(kayitButonu);
        
        doktorGirisButonu = new JButton("DOKTOR GİRİŞİ (PERSONEL)");
        doktorGirisButonu.setBounds(150, 260, 225, 35);
        doktorGirisButonu.setBackground(new Color(230, 126, 34)); 
        doktorGirisButonu.setForeground(Color.WHITE);
        add(doktorGirisButonu);

        girisButonu.addActionListener(e -> {
            String tc = txtTC.getText();
            String sifre = new String(txtSifre.getPassword());
            Hasta hasta = HastaneSistemi.girisYap(tc, sifre);

            if (hasta != null) {
                new RandevuEkrani(hasta).setVisible(true);
                this.dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Hatalı TC veya Şifre!");
            }
        });
 
        kayitButonu.addActionListener(e -> {
            JPanel p = new JPanel(new GridLayout(0, 1));
            JTextField kAd = new JTextField(); JTextField kSoyad = new JTextField();
            JTextField kTc = new JTextField(); JTextField kSifre = new JTextField();
            JComboBox<String> cmbSigorta = new JComboBox<>(new String[]{"SGK", "OZEL", "VIP"});

            p.add(new JLabel("Ad:")); p.add(kAd); p.add(new JLabel("Soyad:")); p.add(kSoyad);
            p.add(new JLabel("TC No:")); p.add(kTc); p.add(new JLabel("Şifre:")); p.add(kSifre);
            p.add(new JLabel("Sigorta:")); p.add(cmbSigorta);

            int sonuc = JOptionPane.showConfirmDialog(null, p, "Yeni Hasta Kaydı", JOptionPane.OK_CANCEL_OPTION);
            if (sonuc == JOptionPane.OK_OPTION) {
                HastaneSistemi.kullaniciKaydet(kAd.getText(), kSoyad.getText(), kTc.getText(), kSifre.getText(), (String)cmbSigorta.getSelectedItem());
                JOptionPane.showMessageDialog(this, "Kayıt Başarılı!");
            }
        });
        
        doktorGirisButonu.addActionListener(e -> {
            JPanel p = new JPanel(new GridLayout(0, 1));
            JTextField drAd = new JTextField();
            JTextField drSoyad = new JTextField();
            JPasswordField drSifre = new JPasswordField(); 
            
            p.add(new JLabel("Doktor Adı:")); p.add(drAd);
            p.add(new JLabel("Doktor Soyadı:")); p.add(drSoyad);
            // Şifre olarak ID'lerini (örn: 111, 222) girecekler
            p.add(new JLabel("Personel ID (Şifre):")); p.add(drSifre);
            
            int sonuc = JOptionPane.showConfirmDialog(null, p, "Doktor Personel Girişi", JOptionPane.OK_CANCEL_OPTION);
            
            if (sonuc == JOptionPane.OK_OPTION) {
                String sifre = new String(drSifre.getPassword());
                
                Doktor dr = HastaneSistemi.doktorGirisYap(drAd.getText().trim(), drSoyad.getText().trim(), sifre);
                
                if (dr != null) {
                    new DoktorEkrani(dr).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Hatalı bilgi! Doktor bulunamadı.");
                }
            }
        });
    }
}