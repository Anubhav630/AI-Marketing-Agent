import { useState, useRef, useCallback } from 'react';

export function useToast() {
    const [toasts, setToasts] = useState([]);
    const timers = useRef({});

    const removeToast = useCallback((id) => {
        clearTimeout(timers.current[id]);
        delete timers.current[id];
        setToasts((prev) => prev.filter((t) => t.id !== id));
    }, []);

    const addToast = useCallback(
        ({ message, variant }) => {
            const id = Date.now().toString();
            setToasts((prev) => [...prev, { id, message, variant }]);
            timers.current[id] = setTimeout(() => removeToast(id), 5000);
        },
        [removeToast]
    );

    return { toasts, addToast, removeToast };
}
