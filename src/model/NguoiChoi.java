package model;

import java.io.Serializable;
import java.util.Objects;

public class NguoiChoi implements Serializable{

	private int id;
	private String tenDangNhap;
	private String matKhau;
	private String trangThai;

	public NguoiChoi(String tenDangNhap, String matKhau) {
		this.tenDangNhap = tenDangNhap;
		this.matKhau = matKhau;
	}

	public NguoiChoi(String tenDangNhap) {
		this.tenDangNhap = tenDangNhap;
		this.matKhau = "";
	}

	@Override
	public boolean equals(Object b) {
		return this.getTenDangNhap().equals(((NguoiChoi) b).getTenDangNhap()) || this.getId() == ((NguoiChoi) b).getId();
	}

	@Override
	public int hashCode() {
		return Objects.hash(tenDangNhap);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTenDangNhap() {
		return tenDangNhap;
	}

	public void setTenDangNhap(String tenDangNhap) {
		this.tenDangNhap = tenDangNhap;
	}

	public String getMatKhau() {
		return matKhau;
	}

	public void setMatKhau(String matKhau) {
		this.matKhau = matKhau;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}
}