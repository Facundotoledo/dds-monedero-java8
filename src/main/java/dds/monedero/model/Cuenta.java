package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta(double montoInicial, List<Movimiento> movimientos) {
    saldo = montoInicial;
    this.movimientos = movimientos;
  }


  public void poner(double cuanto) {
    this.checkeoMontoPositivo(cuanto);

    if (movimientos.stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los 3 depositos diarios");
    }

    saldo += cuanto;

    agregarMovimiento(LocalDate.now(), cuanto, true);
  }

  public void sacar(double cuanto) {
    this.checkeoMontoPositivo(cuanto);

    if (saldo - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + saldo + " $");
    }
    double limite = 1000 - getMontoExtraidoA(LocalDate.now());;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $1000 diarios, límite: " + limite);
    }

    saldo -= cuanto;

    agregarMovimiento(LocalDate.now(), cuanto, false);
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    movimientos.add(new Movimiento(fecha, cuanto, esDeposito));
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return movimientos.stream()
        .filter(movimiento -> movimiento.fueExtraido(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }
  public void checkeoMontoPositivo(double monto){
    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
  }


  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

  public double getSaldo() {
    return saldo;
  }
}
