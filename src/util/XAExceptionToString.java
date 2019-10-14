package util;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

public class XAExceptionToString {
    /**
     * Converts {@link XAException} error code to string.
     */
    public static String getXAExceptionErrCodeAsString(int errCode) {
        switch(errCode) {
            case XAException.XA_RBROLLBACK: return "XA_RBROLLBACK"; // XA_RBBASE
            case XAException.XA_RBCOMMFAIL: return "XA_RBCOMMFAIL"; // XA_RBBASE + 1
            case XAException.XA_RBDEADLOCK: return "XA_RBDEADLOCK"; // XA_RBBASE + 2
            case XAException.XA_RBINTEGRITY: return "XA_RBINTEGRITY"; // XA_RBBASE + 3
            case XAException.XA_RBOTHER: return "XA_RBOTHER"; // XA_RBBASE + 4
            case XAException.XA_RBPROTO: return "XA_RBPROTO"; // XA_RBBASE + 5
            case XAException.XA_RBTIMEOUT: return "XA_RBTIMEOUT"; // XA_RBBASE + 6
            case XAException.XA_RBTRANSIENT: return "XA_RBTRANSIENT"; // or "XA_RBEND", XA_RBBASE + 7
            case XAException.XA_NOMIGRATE: return "XA_NOMIGRATE"; // 9
            case XAException.XA_HEURHAZ: return "XA_HEURHAZ"; // 8
            case XAException.XA_HEURCOM: return "XA_HEURCOM"; // 7
            case XAException.XA_HEURRB: return "XA_HEURRB"; // 6
            case XAException.XA_HEURMIX: return "XA_HEURMIX"; // 5
            case XAException.XA_RETRY: return "XA_RETRY"; // 4
            case XAException.XA_RDONLY: return "XA_RDONLY"; // 3
            case XAException.XAER_ASYNC: return "XAER_ASYNC"; // -2
            case XAException.XAER_RMERR: return "XAER_RMERR"; // -3
            case XAException.XAER_NOTA: return "XAER_NOTA"; // -4
            case XAException.XAER_INVAL: return "XAER_INVAL"; // -5
            case XAException.XAER_PROTO: return "XAER_PROTO"; // -6
            case XAException.XAER_RMFAIL: return "XAER_RMFAIL"; // -7
            case XAException.XAER_DUPID: return "XAER_DUPID"; // -8
            case XAException.XAER_OUTSIDE: return "XAER_OUTSIDE"; // -9
            default:
                throw new IllegalStateException("Can't determine XAException error code '" + errCode +
                    "' as defined under class " + XAException.class.getName());
        }
    }
}
