/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import model.Cliente;
import model.Os;

/**
 *
 * @author clebe
 */
public class OsDAO {

    Connection conexao = null;
    PreparedStatement ps;
    ResultSet rs;

    String emitirOS = "insert into tbos(tipo,situacao,equipamento,defeito,servico,tecnico,valor,idcli) values(?,?,?,?,?,?,?,?)";
    String buscarOS = "select os,data_os,tipo,situacao,equipamento,defeito,servico,tecnico,valor,idcli from tbos where os= ?";
    String maxOs = "select max(os) from tbos";
    String editarOS = "update tbos set tipo=?,situacao=?,equipamento=?,defeito=?,servico=?,tecnico=?,valor=? where os=?";
    String excluirOS = "delete from tbos where os=?";

    public ArrayList<Os> buscarPorNomeCliente(String nomeCliente) {
        ArrayList<Os> osList = new ArrayList<>();
        String sql = "SELECT tbos.os, tbos.data_os, tbclientes.idcli, tbclientes.nomecli, tbos.tipo, tbos.situacao, tbos.equipamento, tbos.defeito, tbos.servico, tbos.tecnico, tbos.valor "
                + "FROM tbos "
                + "INNER JOIN tbclientes ON tbos.idcli = tbclientes.idcli "
                + "WHERE tbclientes.nomecli LIKE ? OR tbos.os = ?";
        try {
            conexao = ModuloConexao.conectar();
            ps = conexao.prepareStatement(sql);
            ps.setString(1, "%" + nomeCliente + "%");
            try {
                int osId = Integer.parseInt(nomeCliente);
                ps.setInt(2, osId);
            } catch (NumberFormatException e) {
                ps.setInt(2, 0);
            }
            rs = ps.executeQuery();

            while (rs.next()) {
                Os os = new Os();
                os.setOs(rs.getInt("os"));
                os.setDataOs(rs.getDate("data_os"));
                os.setTipo(rs.getString("tipo"));
                os.setSituacao(rs.getString("situacao"));
                os.setEquipamento(rs.getString("equipamento"));
                os.setDefeito(rs.getString("defeito"));
                os.setServico(rs.getString("servico"));
                os.setTecnico(rs.getString("tecnico"));
                os.setValor(rs.getDouble("valor"));

                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("idcli"));
                cliente.setNome(rs.getString("nomecli"));
                os.setCliente(cliente);

                osList.add(os);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conexao != null) {
                    conexao.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return osList;
    }

    /**
     * Método responsável pela emissão de uma Ordem de Serviço
     */
    public void emitirOS(Os obj) {
        try {

            conexao = ModuloConexao.conectar();
            ps = conexao.prepareStatement(emitirOS);

            ps.setString(1, obj.getTipo());
            ps.setString(2, obj.getSituacao());
            ps.setString(3, obj.getEquipamento());
            ps.setString(4, obj.getDefeito());
            ps.setString(5, obj.getServico());
            ps.setString(6, obj.getTecnico());
            ps.setDouble(7, obj.getValor());
            ps.setInt(8, obj.getCliente().getId());

            ps.execute();
            ps.close();
            JOptionPane.showMessageDialog(null, "OS emitida com Sucesso!");

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                conexao.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
    }

    /**
     * Método responsável pela pesquisa de uma Ordem de Serviço
     */
    public Os consultarOS(int idOs) {
        try {
            conexao = ModuloConexao.conectar();
            ps = conexao.prepareStatement(buscarOS);
            ps.setInt(1, idOs);

            rs = ps.executeQuery();
            Os obj = new Os();
            if (rs.next()) {

                //obj.setOs(rs.getInt("os"));
                obj.setOs(rs.getInt(1));
                obj.setDataOs(rs.getDate(2));
                obj.setTipo(rs.getString(3));
                obj.setSituacao(rs.getString(4));
                obj.setEquipamento(rs.getString(5));
                obj.setDefeito(rs.getString(6));
                obj.setServico(rs.getString(7));
                obj.setTecnico(rs.getString(8));
                obj.setValor(rs.getDouble(9));
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt(10));
                obj.setCliente(cliente);

                return obj;
            } else {
                JOptionPane.showMessageDialog(null, "Ordem de Serviço não encontrada!");
            }

        } catch (SQLSyntaxErrorException e) {
            JOptionPane.showMessageDialog(null, "OS Inválida");
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                conexao.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        return null;
    }

    /**
     * Método usado para recuperar o número da OS
     */
    public Os recuperarOs() {
        Os obj = new Os();
        try {
            conexao = ModuloConexao.conectar();
            ps = conexao.prepareStatement(maxOs);
            rs = ps.executeQuery();

            if (rs.next()) {
                obj.setOs(rs.getInt(1));
            }
            return obj;
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                conexao.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }
        return null;
    }

    /**
     * Método responsável pela edição de uma Ordem de Seviço
     */
    public void editarOS(Os obj) {
        try {
//String editarOS = "update tbos set tipo=?,situacao=?,equipamento=?,defeito=?,servico=?,tecnico=?,valor=? where os=?";
            conexao = ModuloConexao.conectar();
            ps = conexao.prepareStatement(editarOS);

            ps.setString(1, obj.getTipo());
            ps.setString(2, obj.getSituacao());
            ps.setString(3, obj.getEquipamento());
            ps.setString(4, obj.getDefeito());
            ps.setString(5, obj.getServico());
            ps.setString(6, obj.getTecnico());
            ps.setDouble(7, obj.getValor());
            //ps.setInt(8, obj.getCliente().getId());
            ps.setInt(8, obj.getOs());

            ps.execute();
            ps.close();
            JOptionPane.showMessageDialog(null, "OS alterada com Sucesso!");

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                conexao.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }

    }

    /**
     * Método responsável pela exclusão de uma Ordem de Serviço
     */
    public void excluirOs(Os obj) {

        try {
            conexao = ModuloConexao.conectar();
            ps = conexao.prepareStatement(excluirOS);

            ps.setInt(1, obj.getOs());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(null, "OS excluída com sucesso");
        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                conexao.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex);
            }
        }

    }
}
