package model;

public class BangXepHang extends NguoiChoi {

	private int tongSoDiem;
	private float trungBinhDiemDoiThu;
	private float trungBinhSoNuocDiTranThang;
	private float trungBinhNuocDiTranThua;
	private int soTranDauDaChoi;
	private int soTranThang;
	private int soTranThua;

	public BangXepHang(String tenDangNhap, String matKhau) {
		super(tenDangNhap, matKhau);
	}

	public int getTongSoDiem() {
		return tongSoDiem;
	}

	public void setTongSoDiem(int tongSoDiem) {
		this.tongSoDiem = tongSoDiem;
	}

	public float getTrungBinhDiemDoiThu() {
		return trungBinhDiemDoiThu;
	}

	public void setTrungBinhDiemDoiThu(float trungBinhDiemDoiThu) {
		this.trungBinhDiemDoiThu = trungBinhDiemDoiThu;
	}

	public float getTrungBinhSoNuocDiTranThang() {
		return trungBinhSoNuocDiTranThang;
	}

	public void setTrungBinhSoNuocDiTranThang(float trungBinhSoNuocDiTranThang) {
		this.trungBinhSoNuocDiTranThang = trungBinhSoNuocDiTranThang;
	}

	public float getTrungBinhNuocDiTranThua() {
		return trungBinhNuocDiTranThua;
	}

	public void setTrungBinhNuocDiTranThua(float trungBinhNuocDiTranThua) {
		this.trungBinhNuocDiTranThua = trungBinhNuocDiTranThua;
	}

	public int getSoTranDauDaChoi() {
		return soTranDauDaChoi;
	}

	public void setSoTranDauDaChoi(int soTranDauDaChoi) {
		this.soTranDauDaChoi = soTranDauDaChoi;
	}

	public int getSoTranThang() {
		return soTranThang;
	}

	public void setSoTranThang(int soTranThang) {
		this.soTranThang = soTranThang;
	}

	public int getSoTranThua() {
		return soTranThua;
	}

	public void setSoTranThua(int soTranThua) {
		this.soTranThua = soTranThua;
	}
}