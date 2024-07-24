package com.eoi.grupo5.servicios;

import com.eoi.grupo5.modelos.Pago;
import com.eoi.grupo5.repos.RepoPago;
import org.springframework.stereotype.Service;

@Service
public class ServicioPago extends AbstractBusinessServiceSoloEnt<Pago, Integer, RepoPago> {

    protected ServicioPago(RepoPago repoPago) {
        super(repoPago);
    }
}
