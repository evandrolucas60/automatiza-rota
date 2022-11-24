/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package com.pescaves.infox.telas;

import com.pescaves.infox.dal.ModuloConexao;
import java.io.FileNotFoundException;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

//apache poi
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 *
 * @author Pichau
 */
@SuppressWarnings({"java:S106", "java:S4823", "java:S1192"})
public class TelaOS extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    /**
     * Creates new form TelaOS
     */

    //a linha abaixo cria uma variável para armazenar um texto de acordo com o radio button
    private String tipo;

    private static String[] titles = {"Roteiro", "Rota", "Motorista", "Ajudante", "Cliente", "UF", "Bairro", "Cidade", "Endereço", "Qtd Notas"};
    private static List<OrdemServico> os = new ArrayList<>();

    // Initializing employees data to insert into the excel file
    public TelaOS() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    private void criar_excel() throws FileNotFoundException, IOException {

        // Initializing employees data to insert into the excel file
        os.add(new OrdemServico(cboOsRoteiro.getSelectedItem().toString(), txtOsRota.getText(), txtOsMotorista.getText(), txtOsAjudante.getText(), txtCliNome.getText(), txtCliUF.getText(), txtCliBairro.getText(), txtCliCidade.getText(), txtCliEndereco.getText(), txtOsQtdNotas.getText(), txtOsFoneMotorista.getText(), txtOsFoneAjudante.getText()));

        Workbook wb;

        wb = new XSSFWorkbook();

        Map<String, CellStyle> styles = createStyles(wb);

        Sheet sheet = wb.createSheet("Roteiro");
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);

        //title row
        Row titleRow = sheet.createRow(0);
        titleRow.setHeightInPoints(45);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Pescaves Roteiro");
        titleCell.setCellStyle(styles.get("title"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$J$1"));

        //header row
        Row headerRow = sheet.createRow(1);
        headerRow.setHeightInPoints((short) 14);
        Cell headerCell;
        for (int i = 0; i < titles.length; i++) {
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(titles[i]);
            headerCell.setCellStyle(styles.get("header"));
        }

        int rownum = 2;
        for (int i = 0; i < os.size(); i++) {
            Row row = sheet.createRow(rownum++);
            for (int j = 0; j < titles.length; j++) {
                Cell cell = row.createCell(j);
                if (j == 9) {
                    //the 10th cell contains sum over week days, e.g. SUM(C3:I3)
                    String ref = "C" + rownum + ":I" + rownum;
                    cell.setCellFormula("SUM(" + ref + ")");
                    cell.setCellStyle(styles.get("formula"));
                } else if (j >= 11) {
                    cell.setCellFormula("J" + rownum + "-K" + rownum);
                    cell.setCellStyle(styles.get("formula"));
                } else {
                    cell.setCellStyle(styles.get("cell"));
                }
            }
        }

        //row with totals below
        Row sumRow = sheet.createRow(rownum++);
        sumRow.setHeightInPoints(35);
        Cell cell;
        cell = sumRow.createCell(0);
        cell.setCellStyle(styles.get("formula"));
        cell = sumRow.createCell(1);
        cell.setCellValue("Total de Notas:");
        cell.setCellStyle(styles.get("formula"));

        for (int i = 0; i < os.size(); i++) {

            for (int j = 2; j < 10; j++) {
                cell = sumRow.createCell(j);
                String ref = (char) ('A' + j) + "3:" + (char) ('A' + j) + (os.size() + 2);
                if (j >= 1) {
                    cell.setCellFormula("SUM(" + ref + ")");
                    cell.setCellStyle(styles.get("formula_2"));
                } else {
                    cell.setCellStyle(styles.get("formula"));
                }
            }

        }

        rownum++;
        sumRow = sheet.createRow(rownum++);
        sumRow.setHeightInPoints(25);
        cell = sumRow.createCell(0);
        cell.setCellValue("Total da carga (Kg)");
        cell.setCellStyle(styles.get("formula"));
        cell = sumRow.createCell(1);
        cell.setCellStyle(styles.get("formula_2"));
        sumRow = sheet.createRow(rownum++);
        sumRow.setHeightInPoints(25);

        // Criando roteiro com os dados da OS
        int rowNum = 2;
        for (OrdemServico os : os) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0)
                    .setCellValue(os.getRoteiro());

            row.createCell(1)
                    .setCellValue(os.getRota());

            row.createCell(2)
                    .setCellValue(os.getVeiculo());

            row.createCell(3)
                    .setCellValue(os.getMotorista());

            row.createCell(4)
                    .setCellValue(os.getCliente());

            row.createCell(5)
                    .setCellValue(os.getUf());

            row.createCell(6)
                    .setCellValue(os.getBairro());

            row.createCell(7)
                    .setCellValue(os.getCidade());

            row.createCell(8)
                    .setCellValue(os.getEndereco());

            row.createCell(9)
                    .setCellValue(Integer.valueOf(os.getQtdNotas()));
        }

        // Resize all columns to fit the content size
        for (int i = 0; i < titles.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("C:\\Users\\Pichau\\Desktop\\Roteiros\\Rota.xlsx");
        wb.write(fileOut);
        fileOut.close();
    }

    private static Map<String, CellStyle> createStyles(Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();
        CellStyle style;
        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short) 18);
        titleFont.setBold(true);
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(titleFont);
        styles.put("title", style);

        Font monthFont = wb.createFont();
        monthFont.setFontHeightInPoints((short) 11);
        monthFont.setColor(IndexedColors.WHITE.getIndex());
        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFont(monthFont);
        style.setWrapText(true);
        styles.put("header", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styles.put("cell", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        styles.put("formula", style);

        style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
        styles.put("formula_2", style);

        return styles;
    }

    private void pesquisar_cliente() {
        String sql = "select idcli as Id, nomecli as Nome, ufcli as UF,cidadecli as Cidade, bairrocli as Bairro, enderecocli as Endereço, rotacli as rota, vendedorcli as vendedor from tbclientes where nomecli like ?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCliPesquisar.getText() + "%");
            rs = pst.executeQuery();
            tblClientes.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void setar_campos() {
        int setar = tblClientes.getSelectedRow();
        txtCliId.setText(tblClientes.getModel().getValueAt(setar, 0).toString());
        txtCliNome.setText(tblClientes.getModel().getValueAt(setar, 1).toString());
        txtCliUF.setText(tblClientes.getModel().getValueAt(setar, 2).toString());
        txtCliCidade.setText(tblClientes.getModel().getValueAt(setar, 3).toString());
        txtCliBairro.setText(tblClientes.getModel().getValueAt(setar, 4).toString());
        txtCliEndereco.setText(tblClientes.getModel().getValueAt(setar, 5).toString());
        txtOsRota.setText(tblClientes.getModel().getValueAt(setar, 6).toString());
        txtOsVendedor.setText(tblClientes.getModel().getValueAt(setar, 7).toString());

        if ("ROTA/04 - 18B/C".equals(tblClientes.getModel().getValueAt(setar, 6).toString())) {
            txtOsMotorista.setText("JUNIOR");
            txtOsAjudante.setText("GABRIEL");
            txtOsFoneMotorista.setText("(81)98738-4154");
            txtOsFoneAjudante.setText("(81)99447-0093");

        }

        if ("ROTA/05 - 17B/C".equals(tblClientes.getModel().getValueAt(setar, 6).toString())) {
            txtOsMotorista.setText("LUCIANO");
            txtOsAjudante.setText("ROMARIO");
            txtOsFoneMotorista.setText("(81)98701-6833");
            txtOsFoneAjudante.setText("(81)98617-7437");
        }

        if ("ROTA/03 - 22B/C".equals(tblClientes.getModel().getValueAt(setar, 6).toString())) {
            txtOsMotorista.setText("DANIEL");
            txtOsAjudante.setText("PETINHA");
            txtOsFoneMotorista.setText("(81)98348-4826");
            txtOsFoneAjudante.setText("0000-0000");
        }

        if ("ROTA/02 - 21B/C".equals(tblClientes.getModel().getValueAt(setar, 6).toString())) {
            txtOsMotorista.setText("SIDNEY");
            txtOsAjudante.setText("DEVSON");
            txtOsFoneMotorista.setText("(81)98644-5383");
            txtOsFoneAjudante.setText("0000-0000");
        }

        if ("ROTA/01 - 21B/C".equals(tblClientes.getModel().getValueAt(setar, 6).toString())) {
            txtOsMotorista.setText("HEITOR");
            txtOsAjudante.setText("ANDRE");
            txtOsFoneMotorista.setText("(81)99913-6612");
            txtOsFoneAjudante.setText("0000-0000");
        }

    }

    private void emitir_os() {
        String sql = "insert into tbos(tipo,roteiro,pedido,vendedor,motorista,veiculo,rota,dataEtrega,idcli) values(?,?,?,?,?,?,?,?,?)";
        try {
            pst = conexao.prepareCall(sql);
            pst.setString(1, tipo);
            pst.setString(2, cboOsRoteiro.getSelectedItem().toString());
            pst.setString(3, txtOsPedido.getText());
            pst.setString(4, txtOsVendedor.getText());
            pst.setString(5, txtOsMotorista.getText());
            pst.setString(6, txtOsVeiculo.getText());
            pst.setString(7, txtOsRota.getText());
            pst.setString(8, txtOsDataEntrega.getText());
            pst.setString(9, txtCliId.getText());

            if ((txtCliId.getText().isEmpty()) || (txtOsPedido.getText().isEmpty()) || (txtOsMotorista.getText().isEmpty()) || (cboOsRoteiro.getSelectedItem().equals(" "))) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Roteiro Emitido com sucesso");
                    //recuperar o número da OS
                    recuperar_os();
                    criar_excel();
                    limpar_campos();

                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void pesquisar_os() {
        //a linha abaixo cria uma caixa de entrada JOptionPane
        String num_os = JOptionPane.showInputDialog("Número do Roteiro");
        String sql = "select * from tbos where os = " + num_os;
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                txtRoteiro.setText(rs.getString(1));
                txtData.setText(rs.getString(2));
                //setando os radio button
                String rbtTipo = rs.getString(3);
                if (rbtTipo.equals("Entrega")) {
                    rbtEntrega.setSelected(true);
                    tipo = "Entrega";
                } else {
                    rbtDevolucao.setSelected(true);
                    tipo = "Devolução";
                }

                cboOsRoteiro.setSelectedItem(rs.getString(4));
                txtOsPedido.setText(rs.getString(5));
                txtOsVendedor.setText(rs.getString(6));
                txtOsMotorista.setText(rs.getString(7));
                txtOsVeiculo.setText(rs.getString(8));
                txtOsRota.setText(rs.getString(9));
                txtOsDataEntrega.setText(rs.getString(10));
                txtCliId.setText(rs.getString(11));

                //evitando problemas 
                btnOsAdicionar.setEnabled(false);
                txtCliPesquisar.setEnabled(false);
                tblClientes.setVisible(false);
                //Ativar demais botões
                btnOsExcluir.setEnabled(true);
                btnOsAlterar.setEnabled(true);

            } else {
                JOptionPane.showMessageDialog(null, "Roteiro não cadastrado");
            }
        } catch (java.sql.SQLSyntaxErrorException e) {
            JOptionPane.showMessageDialog(null, "Roteiro Inválido");
            //System.out.println(e);
        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, e2);
        }
    }

    private void alterar_os() {
        String sql = "update tbos set"
                + " tipo=?,"
                + "roteiro=?,"
                + "pedido=?,"
                + "vendedor=?,"
                + "motorista=?,"
                + "veiculo=?,"
                + "rota=?,"
                + "dataEtrega=?"
                + " where os=?";
        try {
            pst = conexao.prepareCall(sql);
            pst.setString(1, tipo);
            pst.setString(2, cboOsRoteiro.getSelectedItem().toString());
            pst.setString(3, txtOsPedido.getText());
            pst.setString(4, txtOsVendedor.getText());
            pst.setString(5, txtOsMotorista.getText());
            pst.setString(6, txtOsVeiculo.getText());
            pst.setString(7, txtOsRota.getText());
            pst.setString(8, txtOsDataEntrega.getText());
            pst.setString(9, txtRoteiro.getText());

            if ((txtCliId.getText().isEmpty()) || (txtOsPedido.getText().isEmpty()) || (txtOsMotorista.getText().isEmpty()) || (cboOsRoteiro.getSelectedItem().equals(" "))) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Roteiro alterado com sucesso");
                    limpar_campos();

                    //Ativar botões
                    btnOsAdicionar.setEnabled(true);
                    txtCliPesquisar.setEnabled(true);
                    tblClientes.setVisible(true);
                    //desativar botões
                    btnOsExcluir.setEnabled(false);
                    btnOsAlterar.setEnabled(false);

                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void excluir_os() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir este Roteiro?", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tbos where os=?";
            try {
                pst = conexao.prepareCall(sql);
                pst.setString(1, txtRoteiro.getText());
                int apagado = pst.executeUpdate();
                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Roteiro excluído com sucesso");
                    limpar_campos();
                    //Ativar botões
                    btnOsAdicionar.setEnabled(true);
                    txtCliPesquisar.setEnabled(true);
                    tblClientes.setVisible(true);
                    //desativar botões
                    btnOsExcluir.setEnabled(false);
                    btnOsAlterar.setEnabled(false);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    //recuperar OS
    private void recuperar_os() {
        String sql = "select max(os) from tbos";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                txtRoteiro.setText(rs.getString(1));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //limpar campos e gerenciar os botões
    private void limpar_campos() {
        txtCliPesquisar.setText(null);
        txtRoteiro.setText(null);
        txtData.setText(null);
        cboOsRoteiro.setSelectedItem(" ");
        txtOsPedido.setText(null);
        txtOsVendedor.setText(null);
        txtCliId.setText(null);
        txtOsMotorista.setText(null);
        txtOsVeiculo.setText(null);
        txtOsRota.setText(null);
        txtOsDataEntrega.setText(null);
        txtCliNome.setText(null);
        txtCliUF.setText(null);
        txtCliCidade.setText(null);
        txtCliBairro.setText(null);
        txtCliEndereco.setText(null);
        txtOsAjudante.setText(null);
        txtOsQtdNotas.setText(null);
        ((DefaultTableModel) tblClientes.getModel()).setRowCount(0);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtRoteiro = new javax.swing.JTextField();
        txtData = new javax.swing.JTextField();
        rbtDevolucao = new javax.swing.JRadioButton();
        rbtEntrega = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        txtCliPesquisar = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        txtCliId = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cboOsRoteiro = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtOsPedido = new javax.swing.JTextField();
        txtOsVendedor = new javax.swing.JTextField();
        txtOsMotorista = new javax.swing.JTextField();
        txtOsDataEntrega = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtOsVeiculo = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtOsRota = new javax.swing.JTextField();
        btnOsAdicionar = new javax.swing.JButton();
        btnOsPesquisar = new javax.swing.JButton();
        btnOsAlterar = new javax.swing.JButton();
        btnOsExcluir = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtCliNome = new javax.swing.JTextField();
        txtCliUF = new javax.swing.JTextField();
        txtCliCidade = new javax.swing.JTextField();
        txtCliEndereco = new javax.swing.JTextField();
        txtCliBairro = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtOsAjudante = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtOsFoneMotorista = new javax.swing.JTextField();
        txtOsFoneAjudante = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtOsQtdNotas = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Tela Roteiro");
        setPreferredSize(new java.awt.Dimension(744, 680));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Nº  Roteiro ");

        jLabel2.setText("Data confecção");

        txtRoteiro.setEditable(false);

        txtData.setEditable(false);

        buttonGroup1.add(rbtDevolucao);
        rbtDevolucao.setText("Devolução");
        rbtDevolucao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtDevolucaoActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbtEntrega);
        rbtEntrega.setText("Entrega");
        rbtEntrega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtEntregaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtRoteiro, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(rbtDevolucao)
                        .addGap(18, 18, 18)
                        .addComponent(rbtEntrega)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRoteiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtDevolucao)
                    .addComponent(rbtEntrega))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtCliPesquisar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCliPesquisarKeyReleased(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pescaves/infox/icones/pesquisar_icon.png"))); // NOI18N

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClientesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblClientes);

        jLabel4.setText("*Id");

        txtCliId.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCliId, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtCliPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(txtCliId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel5.setText("*Roteiro");

        cboOsRoteiro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "domingo", "segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado" }));

        jLabel6.setText("*Pedido");

        jLabel7.setText("Vendedor");

        jLabel8.setText("*Motorista");

        jLabel9.setText("Data serviço");

        txtOsMotorista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOsMotoristaActionPerformed(evt);
            }
        });

        jLabel10.setText("Veículo");

        jLabel11.setText("Rota");

        btnOsAdicionar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pescaves/infox/icones/upload_icon.png"))); // NOI18N
        btnOsAdicionar.setToolTipText("Adicionar");
        btnOsAdicionar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOsAdicionar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsAdicionarActionPerformed(evt);
            }
        });

        btnOsPesquisar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pescaves/infox/icones/search_icon.png"))); // NOI18N
        btnOsPesquisar.setToolTipText("Pesquisar");
        btnOsPesquisar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOsPesquisar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsPesquisar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsPesquisarActionPerformed(evt);
            }
        });

        btnOsAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pescaves/infox/icones/edit_icon.png"))); // NOI18N
        btnOsAlterar.setToolTipText("Editar");
        btnOsAlterar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOsAlterar.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsAlterarActionPerformed(evt);
            }
        });

        btnOsExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/pescaves/infox/icones/delete file_icon.png"))); // NOI18N
        btnOsExcluir.setToolTipText("Apagar");
        btnOsExcluir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOsExcluir.setPreferredSize(new java.awt.Dimension(80, 80));
        btnOsExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOsExcluirActionPerformed(evt);
            }
        });

        jLabel12.setText("Cliente");

        jLabel13.setText("UF");

        jLabel14.setText("Cidade");

        jLabel15.setText("Bairro");

        jLabel16.setText("Endereço");

        txtCliNome.setEditable(false);

        txtCliUF.setEditable(false);

        txtCliCidade.setEditable(false);

        txtCliEndereco.setEditable(false);

        txtCliBairro.setEditable(false);

        jLabel17.setText("Ajudante");

        jLabel18.setText("Fone Mot.");

        jLabel19.setText("Fone Aju.");

        jLabel20.setText("Qtd. Notas");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel17)
                                    .addComponent(jLabel10))
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtOsPedido)
                                    .addComponent(txtOsVendedor)
                                    .addComponent(txtOsMotorista)
                                    .addComponent(cboOsRoteiro, 0, 243, Short.MAX_VALUE)
                                    .addComponent(txtOsAjudante)
                                    .addComponent(txtOsVeiculo))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel19)
                                    .addComponent(jLabel20))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtOsRota, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                                    .addComponent(txtOsDataEntrega)
                                    .addComponent(txtOsFoneMotorista)
                                    .addComponent(txtOsFoneAjudante)
                                    .addComponent(txtOsQtdNotas, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel15))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtCliBairro, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                                    .addComponent(txtCliNome))
                                .addGap(21, 21, 21)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel16))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtCliUF, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtCliCidade))
                                    .addComponent(txtCliEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(168, 168, 168)
                .addComponent(btnOsAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnOsPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnOsAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnOsExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboOsRoteiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtOsPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtOsDataEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtOsVendedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(txtOsRota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtOsMotorista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(txtOsFoneMotorista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtOsAjudante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(txtOsFoneAjudante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtOsVeiculo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(txtOsQtdNotas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(txtCliNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCliUF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCliCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(txtCliEndereco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCliBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOsAdicionar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOsPesquisar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOsAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOsExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(107, Short.MAX_VALUE))
        );

        setBounds(0, 0, 744, 680);
    }// </editor-fold>//GEN-END:initComponents

    private void txtCliPesquisarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCliPesquisarKeyReleased
        // chamando o método pesquisar_cliente
        pesquisar_cliente();
    }//GEN-LAST:event_txtCliPesquisarKeyReleased

    private void tblClientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClientesMouseClicked
        // chamando o método setar_campos
        setar_campos();
    }//GEN-LAST:event_tblClientesMouseClicked

    private void rbtDevolucaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtDevolucaoActionPerformed
        // atribuindo um texto a variavel tipo se selecionado
        tipo = "Devolução";
    }//GEN-LAST:event_rbtDevolucaoActionPerformed

    private void rbtEntregaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtEntregaActionPerformed
        // atribuindo um texto a variavel tipo se selecionado
        tipo = "Entrega";
    }//GEN-LAST:event_rbtEntregaActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // Ao abrir o form marcar o radio button entrega
        rbtEntrega.setSelected(true);
        tipo = "Entrega";
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnOsAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsAdicionarActionPerformed
        // chamar o método emitir Os
        emitir_os();
    }//GEN-LAST:event_btnOsAdicionarActionPerformed

    private void btnOsPesquisarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsPesquisarActionPerformed
        // chamar o método pesquisar_os
        pesquisar_os();
    }//GEN-LAST:event_btnOsPesquisarActionPerformed

    private void btnOsAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsAlterarActionPerformed
        // chama o método alterar_os
        alterar_os();
    }//GEN-LAST:event_btnOsAlterarActionPerformed

    private void btnOsExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOsExcluirActionPerformed
        // chamando o método excluir_os
        excluir_os();
    }//GEN-LAST:event_btnOsExcluirActionPerformed

    private void txtOsMotoristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOsMotoristaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOsMotoristaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOsAdicionar;
    private javax.swing.JButton btnOsAlterar;
    private javax.swing.JButton btnOsExcluir;
    private javax.swing.JButton btnOsPesquisar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cboOsRoteiro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rbtDevolucao;
    private javax.swing.JRadioButton rbtEntrega;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtCliBairro;
    private javax.swing.JTextField txtCliCidade;
    private javax.swing.JTextField txtCliEndereco;
    private javax.swing.JTextField txtCliId;
    private javax.swing.JTextField txtCliNome;
    private javax.swing.JTextField txtCliPesquisar;
    private javax.swing.JTextField txtCliUF;
    private javax.swing.JTextField txtData;
    private javax.swing.JTextField txtOsAjudante;
    private javax.swing.JTextField txtOsDataEntrega;
    private javax.swing.JTextField txtOsFoneAjudante;
    private javax.swing.JTextField txtOsFoneMotorista;
    private javax.swing.JTextField txtOsMotorista;
    private javax.swing.JTextField txtOsPedido;
    private javax.swing.JTextField txtOsQtdNotas;
    private javax.swing.JTextField txtOsRota;
    private javax.swing.JTextField txtOsVeiculo;
    private javax.swing.JTextField txtOsVendedor;
    private javax.swing.JTextField txtRoteiro;
    // End of variables declaration//GEN-END:variables
}
