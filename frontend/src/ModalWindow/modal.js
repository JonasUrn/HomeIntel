import { useState } from "react";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import Modal from "@mui/material/Modal";
import Radio from "@mui/material/Radio";
import { RadioGroup } from "@mui/material";
import FormControlLabel from "@mui/material/FormControlLabel";
import styles from "./modal.module.css";
import ax from "axios";

const questions = {
    1: "Distance from city center",
    2: "Building year",
    3: "Floor number",
    4: "Parking space"
};

// Color mapping for each radio button
const colorMap = {
    r: "red",
    y: "orange",
    g: "green"
};

const ModalWindow = ({ isOpen, onClose, data, isLink }) => {
    const [selectedValues, setSelectedValues] = useState({});
    const [error, setError] = useState(null);

    const handleChange = (event) => {
        const [color, key] = event.target.value.split("_");
        setSelectedValues(prev => ({ ...prev, [key]: event.target.value }));
    };

    const modifySelection = () => {
        var newParams = {}
        for (const [key, value] of Object.entries(selectedValues)) {
            newParams[`${questions[key]}`] = value[0];
        }
        return newParams;
    }

    const submitHandler = async () => {
        try {
            var response = null;
            var newParams = modifySelection();
            console.log(newParams);
            if (isLink) {
                var response = await ax.post("http://localhost:8080/api/evaluate/scraper", {
                    data: data,
                    selectedValues: newParams,
                });
                console.log(`Response: ${Object.keys(response)}`);
            } else {
                response = await ax.post("http://localhost:8080/api/evaluate/prompt", {
                    prompt: data,
                    selectedValues: newParams
                });
            }

            console.log("Response Data:", response.data);
        } catch (err) {
            setError("Error: " + (err.response?.status || err.message));
        }
    };


    return (
        <Modal
            open={isOpen}
            onClose={onClose}
            aria-labelledby="modal-modal-title"
            aria-describedby="modal-modal-description"
            className={styles.modalWindow}
        >
            <Box
                sx={{
                    position: "absolute",
                    top: "50%",
                    left: "50%",
                    transform: "translate(-50%, -50%)",
                    width: 400,
                    bgcolor: "background.paper",
                    boxShadow: 24,
                    p: 4,
                    borderRadius: 2,
                }}
            >
                <div className={styles.modalBoxDiv}>
                    <Typography id="modal-modal-title" variant="h6">
                        Let us know your preferences
                    </Typography>

                    <table style={{ width: "100%", tableLayout: "fixed", marginTop: "10px" }}>
                        <thead>
                            <tr>
                                <th>Questions</th>
                                <th>Importance</th>
                            </tr>
                        </thead>
                        <tbody>
                            {Object.keys(questions).map((key) => (
                                <tr key={key}>
                                    <td>{questions[key]}</td>
                                    <td>
                                        <RadioGroup
                                            value={selectedValues[key] || ""}
                                            onChange={handleChange}
                                            row
                                            style={{ display: "flex", justifyContent: "center" }}
                                        >
                                            {["r", "y", "g"].map((color) => (
                                                <FormControlLabel
                                                    key={color}
                                                    value={`${color}_${key}`}
                                                    control={
                                                        <Radio
                                                            sx={{
                                                                color: colorMap[color],
                                                                "&.Mui-checked": { color: colorMap[color] }
                                                            }}
                                                        />
                                                    }
                                                    label=""
                                                />
                                            ))}
                                        </RadioGroup>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    {error && <Typography color="error">{error}</Typography>}

                    <Button onClick={submitHandler} sx={{ mt: 2 }} variant="contained" className={styles.btn}>
                        Submit
                    </Button>
                    <Button onClick={onClose} sx={{ mt: 2 }} variant="contained" className={styles.btn}>
                        Close
                    </Button>
                </div>
            </Box>
        </Modal>
    );
};

export default ModalWindow;
